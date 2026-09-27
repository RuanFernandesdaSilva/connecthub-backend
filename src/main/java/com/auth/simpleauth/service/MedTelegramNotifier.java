package com.auth.simpleauth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MedTelegramNotifier {

    @Value("${telegram.bot.med.token}")
    private String botToken;

    private final RestTemplate restTemplate = new RestTemplate();

    @Async("telegramAsyncExecutor")
    public void enviarNotificacao(String chatId, String mensagem) {
        enviarPayload(chatId, mensagem, null);
    }

    @Async("telegramAsyncExecutor")
    public void enviarNotificacaoComBotao(String chatId, String mensagem, Long idDose) {
        Map<String, Object> botao = HashMap.newHashMap(2);
        botao.put("text", "✅ Tomei o Remédio");
        botao.put("callback_data", "TOMAR_DOSE:" + idDose);

        List<List<Map<String, Object>>> keyboard = List.of(List.of(botao));
        Map<String, Object> replyMarkup = Map.of("inline_keyboard", keyboard);

        enviarPayload(chatId, mensagem, replyMarkup);
    }

    private void enviarPayload(String chatId, String mensagem, Map<String, Object> replyMarkup) {
        if (chatId == null || chatId.isBlank()) return;

        String url = "https://api.telegram.org/bot" + botToken + "/sendMessage";

        Map<String, Object> body = new HashMap<>();
        body.put("chat_id", chatId);
        body.put("text", mensagem);
        body.put("parse_mode", "HTML");

        if (replyMarkup != null) {
            body.put("reply_markup", replyMarkup);
        }

        try {
            restTemplate.postForObject(url, body, String.class);
        } catch (Exception e) {
            System.err.println("❌ Erro ao enviar mensagem via TelegramNotifier: " + e.getMessage());
        }
    }
}