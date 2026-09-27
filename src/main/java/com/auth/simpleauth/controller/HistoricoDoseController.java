package com.auth.simpleauth.controller;

import com.auth.simpleauth.dto.DoseHistoricoDto;
import com.auth.simpleauth.entity.Familiar;
import com.auth.simpleauth.entity.Usuario;
import com.auth.simpleauth.service.HistoricoDoseService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/historico")

public class HistoricoDoseController {

    private final HistoricoDoseService historicoDoseService;

    public HistoricoDoseController(HistoricoDoseService historicoDoseService) {
        this.historicoDoseService = historicoDoseService;
    }

    @GetMapping("/idoso/{idIdoso}")
    public ResponseEntity<?> consultarHistoricoMedicamentos(@PathVariable Long idIdoso, HttpSession session) {
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuario");

        if (usuarioLogado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Acesso nao autorizado! Efetue o login no sistema.");
        }

        if (!(usuarioLogado instanceof Familiar familiar)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Apenas familiares têm permissão para acessar esta funcionalidade.");
        }

        try {
            List<DoseHistoricoDto> historico = historicoDoseService.buscarHistoricoPorFamiliarEIdoso(familiar.getId(), idIdoso);
            return ResponseEntity.ok(historico);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }
}