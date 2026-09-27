package com.auth.simpleauth.service;

import com.auth.simpleauth.repository.FamiliarRepository;
import com.auth.simpleauth.repository.IdosoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class EventoDeepLinkPollingService {

    @Value("${telegram.bot.evento.token}")
    private String botToken;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Value("${telegram.bot.hub.username:ConnectHubSpoke_bot}")
    private String botHubUsername;

    private final RestTemplate restTemplate = new RestTemplate();
    private final IdosoRepository idosoRepository;
    private final FamiliarRepository familiarRepository;

    private long lastUpdateId = 0;

    public EventoDeepLinkPollingService(IdosoRepository idosoRepository,
                                        FamiliarRepository familiarRepository) {
        this.idosoRepository = idosoRepository;
        this.familiarRepository = familiarRepository;
    }

    @Scheduled(fixedRate = 3000)
    public void verificarAtualizacoesEvento() {
        if (botToken == null || botToken.isBlank()) return;

        String url = String.format("https://api.telegram.org/bot%s/getUpdates?offset=%d", botToken, lastUpdateId + 1);

        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response != null && Boolean.TRUE.equals(response.get("ok"))) {
                List<Map<String, Object>> result = (List<Map<String, Object>>) response.get("result");

                for (Map<String, Object> update : result) {
                    lastUpdateId = ((Number) update.get("update_id")).longValue();

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
        } catch (Exception ignored) {
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
                                    enviarConfirmacaoEVoltar(chatId, idoso.getNome(), token);
                                },
                                () -> enviarNotificacao(chatId, "⚠️ Não encontramos seu cadastro. Peça ajuda a um familiar.")
                        );
                    } else if (token.startsWith("FAMILIAR_")) {
                        Long familiarId = Long.parseLong(token.replace("FAMILIAR_", ""));
                        familiarRepository.findById(familiarId).ifPresentOrElse(
                                familiar -> {
                                    familiar.setTelegramChatId(chatId);
                                    familiarRepository.save(familiar);
                                    enviarConfirmacaoEVoltar(chatId, familiar.getNome(), token);
                                },
                                () -> enviarNotificacao(chatId, "⚠️ Cadastro não encontrado.")
                        );
                    }
                } catch (Exception e) {
                    enviarNotificacao(chatId, "⚠️ Não conseguimos conectar. Tente pelo aplicativo novamente.");
                }
            } else {
                enviarNotificacao(chatId, "👋 Olá!\nPara ativar os avisos de compromissos e consultas, abra o nosso aplicativo e toque no botão de conectar.");
            }
        }
    }

    private void enviarConfirmacaoEVoltar(String chatId, String nome, String token) {
        String url = String.format("https://api.telegram.org/bot%s/sendMessage", botToken);

        String baseUrl = frontendUrl != null ? frontendUrl.trim().replaceAll("/+$", "") : "";

        // ALTERADO: eventos.html -> evento-bot.html
        String urlRetorno = String.format("%s/webapp/evento-bot.html?token=%s&ativado=true&hubUsername=%s",
                baseUrl, token, botHubUsername);

        String texto = String.format(
                "🎉 <b>TUDO CERTO, %s!</b> 🎉\n\n" +
                        "✅ Seus avisos de <b>Compromissos e Consultas</b> foram ativados!\n\n" +
                        "Toque no botão azul abaixo para continuar:\n\n" +
                        "👇 <b>TOQUE NO BOTÃO ABAIXO</b> 👇", nome);

        Map<String, Object> btnAbrirPainel = Map.of(
                "text", "👉 CONTINUAR ",
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
            System.err.println("Erro ao enviar mensagem de confirmação (Evento): " + e.getMessage());
        }
    }

    private void enviarNotificacao(String chatId, String mensagem) {
        String url = String.format("https://api.telegram.org/bot%s/sendMessage", botToken);
        Map<String, Object> body = Map.of("chat_id", chatId, "text", mensagem, "parse_mode", "HTML");
        try {
            restTemplate.postForObject(url, body, String.class);
        } catch (Exception ignored) {}
    }
}