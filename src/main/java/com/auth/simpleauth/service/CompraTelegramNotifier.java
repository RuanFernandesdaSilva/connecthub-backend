package com.auth.simpleauth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class CompraTelegramNotifier {

    @Value("${telegram.bot.compra.token}")
    private String botToken;

    private final RestTemplate restTemplate = new RestTemplate();

    @Async("telegramAsyncExecutor")
    public void enviarAlertaCompra(String chatId, String item) {
        if (chatId == null || chatId.isBlank()) return;

        String url = "https://api.telegram.org/bot" + botToken + "/sendMessage";
        String mensagem = String.format("🛒 <b>Alerta de Compras</b>\n\nO item <b>%s</b> foi adicionado à lista ou precisa de reposição.", item);

        Map<String, Object> body = Map.of(
                "chat_id", chatId,
                "text", mensagem,
                "parse_mode", "HTML"
        );

        try {
            restTemplate.postForObject(url, body, String.class);
        } catch (Exception e) {
            System.err.println("❌ Erro ao enviar item via NotificaCompraBot: " + e.getMessage());
        }
    }
}