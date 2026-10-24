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
        Familiar familiar = dto.toEntity();

        // TRAVA DE SEGURANÇA NO CADASTRO:
        // Garante que o telegramChatId não venha preenchido incorretamente pelo frontend no cadastro.
        // O vínculo deve ser feito EXCLUSIVAMENTE pelo fluxo de deep link (/start FAMILIAR_ID) no Telegram.
        if (dto.getTelegramChatId() == null || dto.getTelegramChatId().isBlank()) {
            familiar.setTelegramChatId(null);
        }

        return repository.save(familiar);
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

        if (dto.getNome() != null && !dto.getNome().isBlank()) {
            familiar.setNome(dto.getNome());
        }
        if (dto.getTelefone() != null && !dto.getTelefone().isBlank()) {
            familiar.setTelefone(dto.getTelefone());
        }
        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            familiar.setEmail(dto.getEmail());
        }
        if (dto.getSenha() != null && !dto.getSenha().isBlank()) {
            familiar.setSenha(dto.getSenha());
        }

        // TRAVA DE SEGURANÇA NA ATUALIZAÇÃO:
        // Evita que updates parciais de perfil vindos do frontend enviando string vazia ("")
        // ou nulo limpem o chatId que o bot do Telegram salvou anteriormente.
        if (dto.getTelegramChatId() != null && !dto.getTelegramChatId().isBlank()) {
            familiar.setTelegramChatId(dto.getTelegramChatId());
        }

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