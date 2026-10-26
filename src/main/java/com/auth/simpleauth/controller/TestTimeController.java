package com.auth.simpleauth.controller;

import com.auth.simpleauth.config.TestableClock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/teste/tempo")
public class TestTimeController {

    @Autowired
    private TestableClock clock;

    /**
     * Exibe o horário atual simulado pela aplicação.
     * Uso: GET /teste/tempo/agora
     */
    @GetMapping("/agora")
    public String obterHorarioAtual() {
        return "🕒 Horário atual da aplicação: " + LocalDateTime.now(clock);
    }

    /**
     * Avança X horas no relógio.
     * Uso: GET /teste/tempo/avancar-horas/2
     */
    @GetMapping("/avancar-horas/{horas}")
    public String avancarHoras(@PathVariable long horas) {
        clock.adiantarHoras(horas);
        return "⏩ Tempo adiantado em " + horas + " hora(s)! Horário atual da aplicação: " + LocalDateTime.now(clock);
    }

    /**
     * Avança X minutos no relógio.
     * Uso: GET /teste/tempo/avancar-minutos/15
     */
    @GetMapping("/avancar-minutos/{minutos}")
    public String avancarMinutos(@PathVariable long minutos) {
        clock.adiantarMinutos(minutos);
        return "⏩ Tempo adiantado em " + minutos + " minuto(s)! Horário atual da aplicação: " + LocalDateTime.now(clock);
    }

    /**
     * Reseta o relógio para o horário real oficial.
     * Uso: GET /teste/tempo/resetar
     */
    @GetMapping("/resetar")
    public String resetar() {
        clock.resetar();
        return "🔄 Relógio resetado para o horário real: " + LocalDateTime.now(clock);
    }
}