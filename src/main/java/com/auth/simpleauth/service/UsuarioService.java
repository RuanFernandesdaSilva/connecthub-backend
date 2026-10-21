package com.auth.simpleauth.service;

import com.auth.simpleauth.entity.Usuario;
import com.auth.simpleauth.repository.UsuarioRepository;
import com.auth.simpleauth.repository.VinculoFamiliarRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository loginRepository;
    private final VinculoFamiliarRepository vinculoRepository;

    public UsuarioService(UsuarioRepository loginRepository, VinculoFamiliarRepository vinculoRepository) {
        this.loginRepository = loginRepository;
        this.vinculoRepository = vinculoRepository;
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> findByEmailAndSenha(String email, String senha) {
        return this.loginRepository.findByEmailAndSenha(email, senha);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorId(Long id) {
        return loginRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> findByEmail(String email) {
        return this.loginRepository.findByEmail(email);
    }

    @Transactional
    public void criaNovoUsuario(Usuario novoUsuario) {
        this.loginRepository.save(novoUsuario);
    }

    @Transactional
    public void deletarUsuario(Usuario usuario) {
        this.loginRepository.delete(usuario);
    }

    // EXCLUIR CONTA DEFINITIVA COM DESVINCULAÇÃO COMPLETA
    @Transactional
    public void deletarPorId(Long id) {
        // PASSO 1: Desconectar o Telegram
        loginRepository.limparTelegramChatId(id);

        // PASSO 2: Deletar todos os vínculos na tabela tbl_vinculo_familiar_idoso
        if (vinculoRepository != null) {
            vinculoRepository.deleteByFamiliarIdOrIdosoId(id, id);
        }

        // PASSO 3: Buscar a entidade usuario atualizada do banco
        Usuario usuario = loginRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // PASSO 4: Deletar o usuário e efetivar a exclusão no PostgreSQL
        loginRepository.delete(usuario);
        loginRepository.flush();
    }

    // ALTERAR NOME
    @Transactional
    public Usuario atualizarNome(Long id, String nome) {
        loginRepository.atualizarApenasNome(id, nome);
        return loginRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    // ALTERAR EMAIL
    @Transactional
    public Usuario atualizarEmail(Long id, String email) {
        loginRepository.atualizarApenasEmail(id, email);
        return loginRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    // ALTERAR TELEFONE
    @Transactional
    public Usuario atualizarTelefone(Long id, String telefone) {
        loginRepository.atualizarApenasTelefone(id, telefone);
        return loginRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    // ALTERAR SENHA
    @Transactional
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
    @Transactional
    public Usuario atualizarFoto(Long id, String imagemUrl) {
        loginRepository.atualizarApenasFoto(id, imagemUrl);
        return loginRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }
}