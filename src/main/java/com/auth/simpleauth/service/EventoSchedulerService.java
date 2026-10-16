package com.auth.simpleauth.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class EventoSchedulerService {

    private final EventoTelegramNotifier eventoTelegramNotifier;

    public EventoSchedulerService(EventoTelegramNotifier eventoTelegramNotifier) {
        this.eventoTelegramNotifier = eventoTelegramNotifier;
    }

    @Scheduled(fixedRate = 60000)
    public void verificarEventosProximos() {
        // Lógica futura: consultar a tabela de Eventos/Consultas do banco
        // e disparar via eventoTelegramNotifier.enviarLembreteEvento(...)
    }
}