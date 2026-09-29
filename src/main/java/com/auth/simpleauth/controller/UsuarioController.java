package com.auth.simpleauth.controller;

import com.auth.simpleauth.dto.*;
import com.auth.simpleauth.entity.Familiar;
import com.auth.simpleauth.entity.Idoso;
import com.auth.simpleauth.entity.Usuario;
import com.auth.simpleauth.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/usuarios")

public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioPerfilDto> buscarUsuario(@PathVariable Long id) {

        return usuarioService.buscarPorId(id)
                .map(usuario -> new UsuarioPerfilDto(
                        usuario.getId(),
                        usuario.getNome(),
                        usuario.getEmail(),
                        usuario.getTelefone(),
                        usuario.getPerfil(),
                        usuario.getImagemUrl()
                ))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // CONSULTAR STATUS DA CONEXÃO DO TELEGRAM (Polling)
    @GetMapping("/{id}/status-telegram")
    public ResponseEntity<Map<String, Object>> checarStatusTelegram(@PathVariable Long id) {
        return usuarioService.buscarPorId(id)
                .map(usuario -> {
                    boolean conectado = false;
                    boolean chatIdValido = usuario.getTelegramChatId() != null && !usuario.getTelegramChatId().isBlank();

                    if (usuario instanceof Idoso idoso) {
                        conectado = chatIdValido && idoso.isTodosBotsConectados();
                    } else if (usuario instanceof Familiar familiar) {
                        conectado = chatIdValido && familiar.isBotMedicamentosConectado();
                    }

                    Map<String, Object> response = Map.of(
                            "telegramConectado", conectado,
                            "tipo", usuario instanceof Idoso ? "IDOSO" : "FAMILIAR"
                    );
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ALTERAR NOME
    @PutMapping("/{id}/nome")
    public ResponseEntity<UsuarioPerfilDto> atualizarNome(
            @PathVariable Long id,
            @RequestBody AtualizarNomeDto dto) {

        Usuario usuario = usuarioService.atualizarNome(id, dto.getNome());

        UsuarioPerfilDto resposta = new UsuarioPerfilDto(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTelefone(),
                usuario.getPerfil(),
                usuario.getImagemUrl()
        );

        return ResponseEntity.ok(resposta);
    }

    // ALTERAR EMAIL
    @PutMapping("/{id}/email")
    public ResponseEntity<UsuarioPerfilDto> atualizarEmail(
            @PathVariable Long id,
            @RequestBody AtualizarEmailDto dto) {

        Usuario usuario = usuarioService.atualizarEmail(id, dto.getEmail());

        UsuarioPerfilDto resposta = new UsuarioPerfilDto(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTelefone(),
                usuario.getPerfil(),
                usuario.getImagemUrl()
        );

        return ResponseEntity.ok(resposta);
    }

    // ALTERAR TELEFONE
    @PutMapping("/{id}/telefone")
    public ResponseEntity<UsuarioPerfilDto> atualizarTelefone(
            @PathVariable Long id,
            @RequestBody AtualizarTelefoneDto dto) {

        Usuario usuario = usuarioService.atualizarTelefone(id, dto.getTelefone());

        UsuarioPerfilDto resposta = new UsuarioPerfilDto(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTelefone(),
                usuario.getPerfil(),
                usuario.getImagemUrl()
        );

        return ResponseEntity.ok(resposta);
    }

    // ALTERAR SENHA
    @PutMapping("/{id}/senha")
    public ResponseEntity<String> atualizarSenha(
            @PathVariable Long id,
            @RequestBody AtualizarSenhaDto dto) {

        usuarioService.atualizarSenha(
                id,
                dto.getSenhaAtual(),
                dto.getNovaSenha()
        );

        return ResponseEntity.ok("Senha alterada com sucesso");
    }

    // ALTERAR FOTO
    @PutMapping("/{id}/foto")
    public ResponseEntity<UsuarioPerfilDto> atualizarFoto(
            @PathVariable Long id,
            @RequestBody AtualizarFotoDto dto) {

        Usuario usuario = usuarioService.atualizarFoto(
                id,
                dto.getImagemUrl()
        );

        UsuarioPerfilDto resposta = new UsuarioPerfilDto(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTelefone(),
                usuario.getPerfil(),
                usuario.getImagemUrl()
        );

        return ResponseEntity.ok(resposta);
    }

    // EXCLUIR CONTA
    @DeleteMapping("/{id}")
    public ResponseEntity<String> excluirConta(@PathVariable Long id) {

        usuarioService.deletarPorId(id);

        return ResponseEntity.ok("Conta excluída com sucesso");
    }
}