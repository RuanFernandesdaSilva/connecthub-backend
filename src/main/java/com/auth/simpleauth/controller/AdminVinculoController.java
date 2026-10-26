package com.auth.simpleauth.controller;

import com.auth.simpleauth.service.AdminVinculoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/vinculos")
public class AdminVinculoController {

    private final AdminVinculoService adminVinculoService;

    public AdminVinculoController(AdminVinculoService adminVinculoService) {
        this.adminVinculoService = adminVinculoService;
    }

    /**
     * Endpoint genérico para vincular qualquer idoso a um familiar via URL.
     * Exemplo de uso: GET /api/admin/vinculos/vincular?idosoId=36&familiarId=37
     */
    @GetMapping("/vincular")
    public ResponseEntity<String> vincularIdosoAFamiliar(
            @RequestParam Long idosoId,
            @RequestParam Long familiarId) {
        String resultado = adminVinculoService.vincularManualmente(idosoId, familiarId);
        return ResponseEntity.ok(resultado);
    }

    /**
     * Endpoint automático que corrige no banco qualquer idoso orfão com vínculo aceito.
     * Alterado para GET para permitir execução direta via navegador.
     * Exemplo de uso: GET /api/admin/vinculos/sincronizar
     */
    @GetMapping("/sincronizar")
    public ResponseEntity<String> sincronizarVinculos() {
        String resultado = adminVinculoService.sincronizarTodosVinculosOrfaos();
        return ResponseEntity.ok(resultado);
    }
}