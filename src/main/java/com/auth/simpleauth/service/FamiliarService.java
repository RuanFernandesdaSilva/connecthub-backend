package com.auth.simpleauth.service;

import com.auth.simpleauth.dto.FamiliarDto;
import com.auth.simpleauth.entity.Familiar;
import com.auth.simpleauth.repository.FamiliarRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class FamiliarService {

    private final FamiliarRepository repository;

    public FamiliarService(FamiliarRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Familiar cadastrar(FamiliarDto dto) {
        return repository.save(dto.toEntity());
    }

    public List<Familiar> listar() {
        return repository.findAll();
    }

    public Optional<Familiar> buscarPorId(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public Familiar atualizar(Long id, FamiliarDto dto) {
        Familiar familiar = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Familiar não encontrado com ID: " + id));

        familiar.setNome(dto.getNome());
        familiar.setTelefone(dto.getTelefone());

        if (dto.getEmail() != null) familiar.setEmail(dto.getEmail());
        if (dto.getSenha() != null) familiar.setSenha(dto.getSenha());
        if (dto.getTelegramChatId() != null) familiar.setTelegramChatId(dto.getTelegramChatId());

        return repository.save(familiar);
    }

    @Transactional
    public void remover(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Familiar não encontrado com ID: " + id);
        }
        repository.deleteById(id);
    }

    @Transactional
    public void atualizarFotoPerfil(Long id, String imagemUrl) {
        Familiar familiar = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Familiar não encontrado com ID: " + id));

        familiar.setImagemUrl(imagemUrl);
        repository.save(familiar);
    }
}