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


    @PostMapping("/avancar-horas/{horas}")
    public String avancarHoras(@PathVariable long horas) {
        clock.adiantarHoras(horas);
        return "Tempo adiantado em " + horas + " horas! Horário atual da aplicação: " + LocalDateTime.now(clock);
    }


    @PostMapping("/avancar-minutos/{minutos}")
    public String avancarMinutos(@PathVariable long minutos) {
        clock.adiantarMinutos(minutos);
        return "Tempo adiantado em " + minutos + " minutos! Horário atual da aplicação: " + LocalDateTime.now(clock);
    }


    @PostMapping("/resetar")
    public String resetar() {
        clock.resetar();
        return "Relógio resetado para o horário real: " + LocalDateTime.now(clock);
    }
}