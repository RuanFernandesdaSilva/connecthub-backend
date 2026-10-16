package com.auth.simpleauth.controller;

import com.auth.simpleauth.service.WebAppActivationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/webapp")

public class WebAppActivationController {

    private final WebAppActivationService activationService;

    public WebAppActivationController(WebAppActivationService activationService) {
        this.activationService = activationService;
    }

    @PostMapping("/ativar-servico")
    public ResponseEntity<?> ativarServico(@RequestBody Map<String, String> payload) {
        String token = payload.get("token");
        String servico = payload.get("servico");

        boolean sucesso = activationService.ativarBotSecundario(token, servico);

        if (sucesso) {
            return ResponseEntity.ok(Map.of("status", "sucesso", "mensagem", "Ativado com sucesso"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("status", "erro", "mensagem", "Falha ao ativar serviço em segundo plano"));
        }
    }
}