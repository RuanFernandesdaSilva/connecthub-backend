package com.auth.simpleauth.service;

import com.auth.simpleauth.entity.Familiar;
import com.auth.simpleauth.enums.StatusDose;
import com.auth.simpleauth.repository.DoseRepository;
import com.auth.simpleauth.repository.FamiliarRepository;
import com.auth.simpleauth.repository.IdosoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class MedDeepLinkPollingService {

    @Value("${telegram.bot.med.token}")
    private String botToken;

    @Value("${app.frontend.url:https://connecthub-frontend-three.vercel.app}")
    private String frontendUrl;

    @Value("${telegram.bot.hub.username:ConnectHubSpoke_bot}")
    private String botHubUsername;

    private final RestTemplate restTemplate = new RestTemplate();
    private final DoseRepository doseRepository;
    private final IdosoRepository idosoRepository;
    private final FamiliarRepository familiarRepository;
    private final MedTelegramNotifier medTelegramNotifier;
    private final DoseService doseService;

    private long lastUpdateId = 0;

    public MedDeepLinkPollingService(DoseRepository doseRepository,
                                     IdosoRepository idosoRepository,
                                     FamiliarRepository familiarRepository,
                                     MedTelegramNotifier medTelegramNotifier,
                                     DoseService doseService) {
        this.doseRepository = doseRepository;
        this.idosoRepository = idosoRepository;
        this.familiarRepository = familiarRepository;
        this.medTelegramNotifier = medTelegramNotifier;
        this.doseService = doseService;
    }

    @Scheduled(fixedRate = 3000)
    @Transactional
    public void verificarAtualizacoesMed() {
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
                        String callbackQueryId = (String) callback.get("id");
                        String data = (String) callback.get("data");

                        if (data != null && data.startsWith("TOMAR_DOSE:")) {
                            Long idDose = Long.parseLong(data.split(":")[1]);
                            processarConfirmacao(callbackQueryId, idDose);
                        }
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
            // Log do erro ao capturar atualizações para diagnóstico do '409 Conflict'
            System.err.println(">>> Erro no Polling do Telegram: " + e.getMessage());
        }
    }

    @Transactional
    public void processarComandoStart(String texto, String chatId) {
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
                                    System.out.println(">>> [VÍNCULO TELEGRAM] Idoso '" + idoso.getNome() + "' (ID: " + idosoId + ") vinculado ao chatId: " + chatId);
                                    enviarConfirmacaoEVoltar(chatId, idoso.getNome(), token);
                                },
                                () -> medTelegramNotifier.enviarNotificacao(chatId, "⚠️ Não encontramos seu cadastro. Peça ajuda a um familiar.")
                        );
                    } else if (token.startsWith("FAMILIAR_")) {
                        Long familiarId = Long.parseLong(token.replace("FAMILIAR_", ""));
                        familiarRepository.findById(familiarId).ifPresentOrElse(
                                familiar -> {
                                    familiar.setTelegramChatId(chatId);
                                    familiarRepository.save(familiar);
                                    System.out.println(">>> [VÍNCULO TELEGRAM] Familiar '" + familiar.getNome() + "' (ID: " + familiarId + ") vinculado ao chatId: " + chatId);
                                    enviarConfirmacaoEVoltar(chatId, familiar.getNome(), token);
                                },
                                () -> medTelegramNotifier.enviarNotificacao(chatId, "⚠️ Cadastro não encontrado.")
                        );
                    }
                } catch (Exception e) {
                    System.err.println(">>> Erro ao processar /start: " + e.getMessage());
                    medTelegramNotifier.enviarNotificacao(chatId, "⚠️ Não conseguimos conectar. Tente pelo aplicativo novamente.");
                }
            } else {
                medTelegramNotifier.enviarNotificacao(chatId, "👋 Olá!\nPara ativar os avisos de remédios, abra o nosso aplicativo e toque no botão de conectar.");
            }
        }
    }

    private void enviarConfirmacaoEVoltar(String chatId, String nome, String token) {
        String url = String.format("https://api.telegram.org/bot%s/sendMessage", botToken);

        String baseUrl = frontendUrl != null ? frontendUrl.trim().replaceAll("/+$", "") : "";

        String urlRetorno = String.format("%s/webapp/medicamento-bot.html?token=%s&ativado=true&hubUsername=%s",
                baseUrl, token, botHubUsername);

        String texto = String.format(
                "🎉 <b>TUDO CERTO, %s!</b> 🎉\n\n" +
                        "✅ Seus avisos de <b>Remédios e Saúde</b> foram ativados!\n\n" +
                        "Toque no botão azul abaixo para continuar:\n\n" +
                        "👇 <b>TOQUE NO BOTÃO ABAIXO</b> 👇", nome);

        Map<String, Object> btnAbrirPainel = Map.of(
                "text", "👉 CONTINUAR",
                "web_app", Map.of("url", urlRetorno)
        );

        Map<String, Object> body = Map.of(
                "chat_id", chatId,
                "text", texto,
                "parse_mode", "HTML",
                "reply_markup", Map.of("inline_keyboard", List.of(List.of(btnAbrirPainel)))
        );

        try {
            restTemplate.postForObject(url, body, String.class);
        } catch (Exception e) {
            System.err.println("Erro ao enviar mensagem de confirmação: " + e.getMessage());
        }
    }

    @Transactional
    public void processarConfirmacao(String callbackQueryId, Long idDose) {
        responderCallback(callbackQueryId, "✅ Remédio registrado com sucesso!");

        doseRepository.findById(idDose).ifPresent(dose -> {
            System.out.println(">>> [CONFIRMAÇÃO DOSE] Processando Dose ID: " + idDose + " | Status atual: " + dose.getStatus());

            if (dose.getStatus() != StatusDose.TOMADO) {

                // 1. Guarda a referência do ID do Idoso e do Medicamento ANTES da alteração de status
                var medicamento = dose.getMedicamento();
                Long idosoId = (medicamento != null && medicamento.getIdoso() != null) ? medicamento.getIdoso().getId() : null;
                String nomeMedicamento = medicamento != null ? medicamento.getNome() : "";

                // 2. Executa a confirmação da dose
                doseService.confirmarDose(idDose);

                // 3. Busca o Idoso diretamente do repositório carregando explicitamente o Familiar via JOIN FETCH
                com.auth.simpleauth.entity.Idoso idoso = null;
                if (idosoId != null) {
                    idoso = idosoRepository.findByIdWithFamiliar(idosoId).orElse(null);
                }

                Familiar familiar = idoso != null ? idoso.getFamiliar() : null;

                String chatIdIdoso = idoso != null ? idoso.getTelegramChatId() : null;
                String chatIdFamiliar = familiar != null ? familiar.getTelegramChatId() : null;

                // 4. Notifica o Idoso
                if (chatIdIdoso != null && !chatIdIdoso.isBlank()) {
                    System.out.println(">>> Notificando IDOSO (" + idoso.getNome() + ") no Chat ID: " + chatIdIdoso);
                    medTelegramNotifier.enviarNotificacao(chatIdIdoso, "🎉 Parabéns! Sua dose foi confirmada com sucesso.");
                } else {
                    System.err.println(">>> ⚠️ AVISO: Idoso nulo ou sem Telegram Chat ID cadastrado!");
                }

                // 5. Notifica o Familiar
                if (chatIdFamiliar != null && !chatIdFamiliar.isBlank()) {
                    if (chatIdFamiliar.equals(chatIdIdoso)) {
                        System.err.println(">>> ⚠️ ALERTA: Chat ID do Familiar (" + chatIdFamiliar + ") é idêntico ao do Idoso! Notificação duplicada evitada.");
                    } else {
                        System.out.println(">>> Notificando FAMILIAR (" + familiar.getNome() + ") no Chat ID: " + chatIdFamiliar);
                        String msgFamiliar = String.format(
                                "✅ O idoso %s confirmou a tomada do medicamento '%s'!",
                                idoso.getNome(),
                                nomeMedicamento
                        );
                        medTelegramNotifier.enviarNotificacao(chatIdFamiliar, msgFamiliar);
                    }
                } else {
                    System.err.println(">>> ⚠️ AVISO AO FAMILIAR NÃO ENVIADO: " +
                            (familiar == null ? "Idoso sem Familiar associado no banco de dados!" : "Familiar '" + familiar.getNome() + "' está com telegramChatId NULO ou VAZIO no banco de dados!"));
                }
            }
        });
    }

    private void responderCallback(String callbackQueryId, String texto) {
        String url = String.format("https://api.telegram.org/bot%s/answerCallbackQuery", botToken);
        Map<String, Object> body = Map.of(
                "callback_query_id", callbackQueryId,
                "text", texto
        );
        try {
            restTemplate.postForObject(url, body, String.class);
        } catch (Exception ignored) {}
    }
}