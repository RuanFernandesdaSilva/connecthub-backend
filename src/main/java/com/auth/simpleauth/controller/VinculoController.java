package com.auth.simpleauth.controller;

import com.auth.simpleauth.dto.*;
import com.auth.simpleauth.service.VinculoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vinculos")
public class VinculoController {

    private final VinculoService vinculoService;

    public VinculoController(VinculoService vinculoService) {
        this.vinculoService = vinculoService;
    }

    @PostMapping("/qrcode")
    public ResponseEntity<String> vincularPorQrCode(@Valid @RequestBody VinculoFamiliarDto dto) {
        vinculoService.vincularDiretoPorQrCode(dto.getIdFamiliar(), dto.getIdIdoso());
        return ResponseEntity.status(HttpStatus.CREATED).body("Vínculo por QR Code realizado e ativado com sucesso!");
    }

    @GetMapping("/idosos-aceitos/{idFamiliar}")
    public ResponseEntity<List<UsuarioVinculadoDto>> listarIdososAceitosPath(@PathVariable Long idFamiliar) {
        return ResponseEntity.ok(vinculoService.listarIdososAceitosDoFamiliar(idFamiliar));
    }

    @GetMapping("/familiar/{idFamiliar}/idosos")
    public ResponseEntity<List<UsuarioVinculadoDto>> listarIdososAprovados(@PathVariable Long idFamiliar) {
        return ResponseEntity.ok(vinculoService.listarIdososAceitosDoFamiliar(idFamiliar));
    }

    @GetMapping("/familiares-aceitos/{idIdoso}")
    public ResponseEntity<List<UsuarioVinculadoDto>> listarFamiliaresAceitosPath(@PathVariable Long idIdoso) {
        return ResponseEntity.ok(vinculoService.listarFamiliaresAceitosDoIdoso(idIdoso));
    }

    @GetMapping("/idoso/{idIdoso}/familiares")
    public ResponseEntity<List<UsuarioVinculadoDto>> listarFamiliaresAprovados(@PathVariable Long idIdoso) {
        return ResponseEntity.ok(vinculoService.listarFamiliaresAceitosDoIdoso(idIdoso));
    }

    @GetMapping("/detalhes")
    public ResponseEntity<VinculoDetalhadoResponseDto> obterDetalhesVinculo(@RequestParam Long idFamiliar, @RequestParam Long idIdoso) {
        return ResponseEntity.ok(vinculoService.obterDetalhesVinculo(idFamiliar, idIdoso));
    }

    @PostMapping("/telegram")
    public ResponseEntity<String> vincularTelegram(@RequestBody VinculoTelegramDto dto) {
        vinculoService.vincularTelegram(dto.getUsuarioId(), dto.getTelegramChatId());
        return ResponseEntity.ok("Telegram vinculado com sucesso ao usuário!");
    }
}