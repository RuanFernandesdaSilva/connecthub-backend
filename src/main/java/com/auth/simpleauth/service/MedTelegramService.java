package com.auth.simpleauth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MedTelegramService {

    @Value("${telegram.chat.id:}")
    private String defaultChatId;

    private final MedTelegramNotifier medTelegramNotifier;

    public MedTelegramService(MedTelegramNotifier medTelegramNotifier) {
        this.medTelegramNotifier = medTelegramNotifier;
    }

    @Async("telegramAsyncExecutor")
    public void enviarNotificacao(String chatIdDestino, String mensagem) {
        String targetChatId = resolverChatId(chatIdDestino);
        if (targetChatId == null) return;

        medTelegramNotifier.enviarNotificacao(targetChatId, mensagem);
    }

    @Async("telegramAsyncExecutor")
    public void enviarNotificacaoComBotao(String chatIdDestino, String mensagem, Long idDose) {
        String targetChatId = resolverChatId(chatIdDestino);
        if (targetChatId == null) return;

        medTelegramNotifier.enviarNotificacaoComBotao(targetChatId, mensagem, idDose);
    }

    @Async("telegramAsyncExecutor")
    public void enviarNotificacao(String mensagem) {
        enviarNotificacao(null, mensagem);
    }

    private String resolverChatId(String chatId) {
        String target = (chatId != null && !chatId.isBlank()) ? chatId : defaultChatId;
        if (target == null || target.isBlank() || target.startsWith("SEU_")) {
            System.err.println("⚠️ Notificação cancelada: Usuário não possui Chat ID cadastrado.");
            return null;
        }
        return target;
    }
}