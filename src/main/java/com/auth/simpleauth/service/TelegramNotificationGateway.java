package com.auth.simpleauth.service;

import org.springframework.stereotype.Service;

@Service
public class TelegramNotificationGateway {

    private final MedTelegramNotifier medNotifier;
    private final EventoTelegramNotifier eventoNotifier;
    private final CompraTelegramNotifier compraNotifier;

    public TelegramNotificationGateway(MedTelegramNotifier medNotifier,
                                       EventoTelegramNotifier eventoNotifier,
                                       CompraTelegramNotifier compraNotifier) {
        this.medNotifier = medNotifier;
        this.eventoNotifier = eventoNotifier;
        this.compraNotifier = compraNotifier;
    }

    public void notificarDoseComBotao(String chatId, String mensagem, Long idDose) {
        medNotifier.enviarNotificacaoComBotao(chatId, mensagem, idDose);
    }

    public void notificarMedicamento(String chatId, String mensagem) {
        medNotifier.enviarNotificacao(chatId, mensagem);
    }

    public void notificarEvento(String chatId, String titulo, String horario) {
        eventoNotifier.enviarLembreteEvento(chatId, titulo, horario);
    }

    public void notificarCompra(String chatId, String item) {
        compraNotifier.enviarAlertaCompra(chatId, item);
    }
}