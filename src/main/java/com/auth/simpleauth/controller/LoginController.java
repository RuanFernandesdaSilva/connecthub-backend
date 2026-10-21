package com.auth.simpleauth.controller;

import com.auth.simpleauth.dto.LoginRequestDto;
import com.auth.simpleauth.dto.LoginResponseDto;
import com.auth.simpleauth.dto.UsuarioCadastroDto;
import com.auth.simpleauth.entity.Usuario;
import com.auth.simpleauth.enums.Perfil;
import com.auth.simpleauth.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/login")
// ⚠️ NOTA: A anotação @CrossOrigin foi removida daqui para não conflitar com CorsConfig.java
public class LoginController {

    private final UsuarioService usuarioService;

    public LoginController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginDto, HttpSession session) {
        Optional<Usuario> usuarioTmp = this.usuarioService.findByEmail(loginDto.getEmail());

        if (usuarioTmp.isPresent()) {
            Usuario usuario = usuarioTmp.get();

            if (loginDto.getSenha().equals(usuario.getSenha())) {
                // Grava o usuário autenticado na HttpSession do Servlet container
                session.setAttribute("usuario", usuario);

                // Retorna o DTO com o estado atualizado da aplicação
                return ResponseEntity.ok(new LoginResponseDto(usuario));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Senha incorreta! 😢");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não cadastrado!");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UsuarioCadastroDto novoUsuarioDto) {
        Optional<Usuario> usuarioExistente = this.usuarioService.findByEmail(novoUsuarioDto.getEmail());

        if (usuarioExistente.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("E-mail já cadastrado!");
        } else {
            this.usuarioService.criaNovoUsuario(novoUsuarioDto.toEntity());
            return ResponseEntity.status(HttpStatus.CREATED).body("Usuário criado com sucesso!");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Até a próxima! 👋🏼");
    }

    @GetMapping
    public ResponseEntity<?> usuarioLogado(HttpSession session) {
        Usuario userSessao = (Usuario) session.getAttribute("usuario");

        if (userSessao != null) {
            Optional<Usuario> userAtualizado = usuarioService.buscarPorId(userSessao.getId());

            if (userAtualizado.isPresent()) {
                Usuario user = userAtualizado.get();
                session.setAttribute("usuario", user); // Atualiza a sessão em memória
                return ResponseEntity.ok(new LoginResponseDto(user));
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Nenhum usuário logado!");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletarUsuario(@PathVariable Long id, HttpSession session) {
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuario");

        if (usuarioLogado != null) {
            if (usuarioLogado.getPerfil() != Perfil.FAMILIAR) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        "É necessário perfil FAMILIAR para efetuar essa operação!");
            } else {
                this.usuarioService.deletarPorId(id);
                return ResponseEntity.ok("Usuário excluído com sucesso!");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    "É necessário estar logado como FAMILIAR para excluir um usuário!");
        }
    }
}