package com.auth.simpleauth.service;

import com.auth.simpleauth.enums.StatusDose;
import com.auth.simpleauth.repository.DoseRepository;
import com.auth.simpleauth.repository.FamiliarRepository;
import com.auth.simpleauth.repository.IdosoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// @Service <-- Removido para desativar a classe mantendo o código como histórico
public class TelegramGatewayPollingService {

    @Value("${telegram.bot.hub.token}")
    private String botToken;

    @Value("${telegram.bot.med.username}")
    private String botMedUsername;

    @Value("${telegram.bot.evento.username}")
    private String botEventoUsername;

    @Value("${telegram.bot.compra.username}")
    private String botCompraUsername;

    private final RestTemplate restTemplate = new RestTemplate();
    private final DoseRepository doseRepository;
    private final IdosoRepository idosoRepository;
    private final FamiliarRepository familiarRepository;
    private final TelegramNotificationGateway notificationGateway;
    private final DoseService doseService;

    private long lastUpdateId = 0;

    public TelegramGatewayPollingService(DoseRepository doseRepository,
                                         IdosoRepository idosoRepository,
                                         FamiliarRepository familiarRepository,
                                         TelegramNotificationGateway notificationGateway,
                                         DoseService doseService) {
        this.doseRepository = doseRepository;
        this.idosoRepository = idosoRepository;
        this.familiarRepository = familiarRepository;
        this.notificationGateway = notificationGateway;
        this.doseService = doseService;
    }

    // @Scheduled(fixedRate = 3000) <-- Removido
    public void escutarAtualizacoesHub() {
        if (botToken == null || botToken.isBlank()) return;

        String url = String.format("https://api.telegram.org/bot%s/getUpdates?offset=%d", botToken, lastUpdateId + 1);

        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response != null && Boolean.TRUE.equals(response.get("ok"))) {
                List<Map<String, Object>> result = (List<Map<String, Object>>) response.get("result");

                for (Map<String, Object> update : result) {
                    lastUpdateId = ((Number) update.get("update_id")).longValue();

                    if (update.containsKey("callback_query")) {
                        Map<String, Object> callback = (Map<String, Object>) update.get("callback_query");
                        processarCallbackQuery(callback);
                    }

                    if (update.containsKey("message")) {
                        Map<String, Object> message = (Map<String, Object>) update.get("message");
                        if (message.containsKey("text") && message.containsKey("chat")) {
                            String texto = (String) message.get("text");
                            Map<String, Object> chat = (Map<String, Object>) message.get("chat");
                            String chatId = String.valueOf(chat.get("id"));
                            processarComandoStart(texto, chatId);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("⚠️ Erro no Polling do Hub Central Telegram: " + e.getMessage());
        }
    }

    private void processarCallbackQuery(Map<String, Object> callback) {
        String callbackId = String.valueOf(callback.get("id"));
        String data = (String) callback.get("data");

        if (data != null && data.startsWith("TOMAR_DOSE:")) {
            Long idDose = Long.parseLong(data.split(":")[1]);
            processarConfirmacao(idDose);
            responderCallbackQuery(callbackId, "Dose confirmada com sucesso!");
        }
    }

    private void responderCallbackQuery(String callbackQueryId, String texto) {
        String url = "https://api.telegram.org/bot" + botToken + "/answerCallbackQuery";
        Map<String, Object> body = HashMap.newHashMap(2);
        body.put("callback_query_id", callbackQueryId);
        body.put("text", texto);

        try {
            restTemplate.postForObject(url, body, String.class);
        } catch (Exception e) {
            System.err.println("Erro ao responder CallbackQuery: " + e.getMessage());
        }
    }

    private void processarComandoStart(String texto, String chatId) {
        if (texto.startsWith("/start")) {
            String[] partes = texto.split(" ");
            if (partes.length > 1) {
                String token = partes[1];
                try {
                    if (token.startsWith("IDOSO_")) {
                        Long idosoId = Long.parseLong(token.replace("IDOSO_", ""));
                        idosoRepository.findById(idosoId).ifPresentOrElse(
                                idoso -> {
                                    idoso.setTelegramChatId(chatId);
                                    idosoRepository.save(idoso);
                                    enviarBotoesConexaoSpokes(chatId, idoso.getNome(), token);
                                },
                                () -> notificationGateway.notificarMedicamento(chatId, String.format("⚠️ Idoso com ID %d não foi encontrado.", idosoId))
                        );
                    } else if (token.startsWith("FAMILIAR_")) {
                        Long familiarId = Long.parseLong(token.replace("FAMILIAR_", ""));
                        familiarRepository.findById(familiarId).ifPresentOrElse(
                                familiar -> {
                                    familiar.setTelegramChatId(chatId);
                                    familiarRepository.save(familiar);
                                    enviarBotoesConexaoSpokes(chatId, familiar.getNome(), token);
                                },
                                () -> notificationGateway.notificarMedicamento(chatId, String.format("⚠️ Familiar com ID %d não foi encontrado.", familiarId))
                        );
                    }
                } catch (Exception e) {
                    notificationGateway.notificarMedicamento(chatId, "⚠️ Código de vinculação inválido.");
                }
            } else {
                notificationGateway.notificarMedicamento(chatId, "👋 Bem-vindo ao assistente!\nClique no botão de conexão no app para vincular sua conta.");
            }
        }
    }

    private void enviarBotoesConexaoSpokes(String chatId, String nomeUsuario, String token) {
        String url = String.format("https://api.telegram.org/bot%s/sendMessage", botToken);

        List<List<Map<String, String>>> inlineKeyboard = List.of(
                List.of(Map.of("text", "💊 Ativar Alertas de Remédios", "url", String.format("https://t.me/%s?start=%s", botMedUsername, token))),
                List.of(Map.of("text", "📅 Ativar Alertas de Eventos", "url", String.format("https://t.me/%s?start=%s", botEventoUsername, token))),
                List.of(Map.of("text", "🛒 Ativar Alertas de Compras", "url", String.format("https://t.me/%s?start=%s", botCompraUsername, token)))
        );

        String textoMensagem = String.format(
                "✅ Olá, <b>%s</b>! Sua conta foi vinculada com sucesso ao <b>ConnectHub</b>.\n\n" +
                        "Para ativar o recebimento das notificações em cada canal, toque nos botões abaixo e aperte <b>COMEÇAR</b> em cada um:",
                nomeUsuario
        );

        Map<String, Object> body = Map.of(
                "chat_id", chatId,
                "text", textoMensagem,
                "parse_mode", "HTML",
                "reply_markup", Map.of("inline_keyboard", inlineKeyboard)
        );

        try {
            restTemplate.postForObject(url, body, String.class);
        } catch (Exception e) {
            System.err.println("Erro ao enviar mensagem com botões do Hub: " + e.getMessage());
        }
    }

    private void processarConfirmacao(Long idDose) {
        doseRepository.findById(idDose).ifPresent(dose -> {
            if (dose.getStatus() != StatusDose.TOMADO) {
                doseService.confirmarDose(idDose);
                String chatIdIdoso = dose.getMedicamento().getIdoso().getTelegramChatId();
                if (chatIdIdoso != null) {
                    notificationGateway.notificarMedicamento(chatIdIdoso, "🎉 Parabéns! Sua dose foi confirmada com sucesso.");
                }
                var familiar = dose.getMedicamento().getIdoso().getFamiliar();
                if (familiar != null && familiar.getTelegramChatId() != null) {
                    String msgFamiliar = String.format(
                            "✅ O idoso <b>%s</b> confirmou a tomada do medicamento '<b>%s</b>'!",
                            dose.getMedicamento().getIdoso().getNome(),
                            dose.getMedicamento().getNome()
                    );
                    notificationGateway.notificarMedicamento(familiar.getTelegramChatId(), msgFamiliar);
                }
            }
        });
    }
}