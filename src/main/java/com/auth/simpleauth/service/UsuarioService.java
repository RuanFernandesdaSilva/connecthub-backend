package com.auth.simpleauth.service;

import com.auth.simpleauth.entity.Usuario;
import com.auth.simpleauth.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository loginRepository;

    public UsuarioService(UsuarioRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    public Optional<Usuario> findByEmailAndSenha(String email, String senha) {
        return this.loginRepository.findByEmailAndSenha(email, senha);
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return loginRepository.findById(id);
    }

    public Optional<Usuario> findByEmail(String email) {
        return this.loginRepository.findByEmail(email);
    }

    public void criaNovoUsuario(Usuario novoUsuario) {
        this.loginRepository.save(novoUsuario);
    }

    public void deletarUsuario(Usuario usuario) {
        this.loginRepository.delete(usuario);
    }

    public void deletarPorId(Long id) {
        this.loginRepository.deleteById(id);
    }

    // ALTERAR NOME
    public Usuario atualizarNome(Long id, String nome) {

        Usuario usuario = loginRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        usuario.setNome(nome);

        return loginRepository.save(usuario);
    }

    // ALTERAR EMAIL
    public Usuario atualizarEmail(Long id, String email) {

        Usuario usuario = loginRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        usuario.setEmail(email);

        return loginRepository.save(usuario);
    }



    // ALTERAR TELEFONE
    public Usuario atualizarTelefone(Long id, String telefone) {

        Usuario usuario = loginRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        usuario.setTelefone(telefone);

        return loginRepository.save(usuario);
    }

    // ALTERAR SENHA
    public Usuario atualizarSenha(Long id, String senhaAtual, String novaSenha) {

        Usuario usuario = loginRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!usuario.getSenha().equals(senhaAtual)) {
            throw new RuntimeException("Senha atual incorreta");
        }

        usuario.setSenha(novaSenha);

        return loginRepository.save(usuario);
    }

    // ALTERAR FOTO
    public Usuario atualizarFoto(Long id, String imagemUrl) {

        Usuario usuario = loginRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        usuario.setImagemUrl(imagemUrl);

        return loginRepository.save(usuario);
    }
}