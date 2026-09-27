package com.auth.simpleauth.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class CompraSchedulerService {

    private final CompraTelegramNotifier compraTelegramNotifier;

    public CompraSchedulerService(CompraTelegramNotifier compraTelegramNotifier) {
        this.compraTelegramNotifier = compraTelegramNotifier;
    }

    @Scheduled(fixedRate = 3600000) // Exemplo: verificação a cada hora
    public void verificarItensParaComprar() {
        // Lógica futura: verificar estoque de produtos/remédios zerando
        // e disparar lembretes via compraTelegramNotifier.enviarAlertaCompra(...)
    }
}