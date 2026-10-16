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

    @PostMapping("/solicitar")
    public ResponseEntity<String> solicitarVinculoParams(@RequestParam Long idFamiliar, @RequestParam Long idIdoso) {
        vinculoService.solicitarVinculo(idFamiliar, idIdoso);
        return ResponseEntity.status(HttpStatus.CREATED).body("Solicitação de vínculo enviada com sucesso!");
    }

    @PostMapping("/solicitar-json")
    public ResponseEntity<String> solicitarVinculoJson(@Valid @RequestBody VinculoFamiliarDto dto) {
        vinculoService.solicitarVinculo(dto.getIdFamiliar(), dto.getIdIdoso());
        return ResponseEntity.status(HttpStatus.CREATED).body("Solicitação de vínculo enviada com sucesso!");
    }

    @PostMapping("/qrcode")
    public ResponseEntity<String> vincularPorQrCode(@Valid @RequestBody VinculoFamiliarDto dto) {
        vinculoService.vincularDiretoPorQrCode(dto.getIdFamiliar(), dto.getIdIdoso());
        return ResponseEntity.status(HttpStatus.CREATED).body("Vínculo por QR Code realizado e ativado com sucesso!");
    }

    @GetMapping("/pedidos-pendentes/{idIdoso}")
    public ResponseEntity<List<PedidoVinculoDto>> listarPedidosPendentesPath(@PathVariable Long idIdoso) {
        return ResponseEntity.ok(vinculoService.listarPedidosPendentesDoIdoso(idIdoso));
    }

    @GetMapping("/idoso/{idIdoso}/pendentes")
    public ResponseEntity<List<PedidoVinculoDto>> listarPedidosPendentes(@PathVariable Long idIdoso) {
        return ResponseEntity.ok(vinculoService.listarPedidosPendentesDoIdoso(idIdoso));
    }

    @PutMapping("/responder/{vinculoId}")
    public ResponseEntity<String> responderSolicitacaoPath(@PathVariable Long vinculoId, @RequestParam boolean aceito) {
        String mensagem = vinculoService.responderSolicitacao(vinculoId, aceito);
        return ResponseEntity.ok(mensagem);
    }

    @PostMapping("/responder")
    public ResponseEntity<String> responderSolicitacaoJson(@RequestBody RespostaPedidoDto dto) {
        String mensagem = vinculoService.responderSolicitacao(dto.getVinculoId(), dto.isAceito());
        return ResponseEntity.ok(mensagem);
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