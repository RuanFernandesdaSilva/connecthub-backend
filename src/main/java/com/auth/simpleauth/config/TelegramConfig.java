package com.auth.simpleauth.config;

import com.auth.simpleauth.telegram.ConnectHubBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class TelegramConfig {

    private static final Logger log = LoggerFactory.getLogger(TelegramConfig.class);

    @Bean
    public TelegramBotsApi telegramBotsApi(ConnectHubBot connectHubBot) throws TelegramApiException {
        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);

        String botToken = connectHubBot.getBotToken();

        // Evita tentar conectar no Telegram se o token for fictício ou nulo/vazio
        if (botToken == null || botToken.isBlank() || botToken.contains("dummy_token")) {
            log.warn("Telegram Bot não registrado: Token ausente ou fictício ({}).", botToken);
            return api;
        }

        try {
            api.registerBot(connectHubBot);
            log.info("Bot do Telegram ({}) registrado com sucesso!", connectHubBot.getBotUsername());
        } catch (TelegramApiException e) {
            // Captura o 'Error removing old webhook' e impede a queda do Spring Boot
            log.error("Falha ao registrar o bot no Telegram: {}. A aplicação continuará rodando.", e.getMessage());
        }

        return api;
    }
}