package com.auth.simpleauth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class EventoTelegramNotifier {

    @Value("${telegram.bot.evento.token}")
    private String botToken;

    private final RestTemplate restTemplate = new RestTemplate();

    @Async("telegramAsyncExecutor")
    public void enviarLembreteEvento(String chatId, String titulo, String horario) {
        if (chatId == null || chatId.isBlank()) return;

        String url = "https://api.telegram.org/bot" + botToken + "/sendMessage";
        String mensagem = String.format("🗓️ <b>Lembrete de Evento</b>\n\n<b>Compromisso:</b> %s\n<b>Horário:</b> %s", titulo, horario);

        Map<String, Object> body = Map.of(
                "chat_id", chatId,
                "text", mensagem,
                "parse_mode", "HTML"
        );

        try {
            restTemplate.postForObject(url, body, String.class);
        } catch (Exception e) {
            System.err.println("❌ Erro ao enviar evento via NotificaEventoBot: " + e.getMessage());
        }
    }
}