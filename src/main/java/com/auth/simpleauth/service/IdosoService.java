package com.auth.simpleauth.service;

import com.auth.simpleauth.dto.IdosoDto;
import com.auth.simpleauth.entity.Familiar;
import com.auth.simpleauth.entity.Idoso;
import com.auth.simpleauth.repository.FamiliarRepository;
import com.auth.simpleauth.repository.IdosoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class IdosoService {

    private final IdosoRepository idosoRepository;
    private final FamiliarRepository familiarRepository;

    public IdosoService(IdosoRepository idosoRepository, FamiliarRepository familiarRepository) {
        this.idosoRepository = idosoRepository;
        this.familiarRepository = familiarRepository;
    }

    @Transactional
    public Idoso cadastrar(IdosoDto dto) {
        Familiar familiar = null;
        if (dto.getIdFamiliar() != null) {
            familiar = familiarRepository.findById(dto.getIdFamiliar())
                    .orElseThrow(() -> new RuntimeException("Familiar não encontrado com ID: " + dto.getIdFamiliar()));
        }


        Idoso novoIdoso = dto.toEntity(familiar);

        return idosoRepository.save(novoIdoso);
    }

    public List<Idoso> listar() {
        return idosoRepository.findAll();
    }

    public Optional<Idoso> buscarPorId(Long id) {
        return idosoRepository.findById(id);
    }

    @Transactional
    public Idoso atualizar(Long id, IdosoDto dto) {
        Idoso idosoEncontrado = idosoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Idoso não encontrado com ID: " + id));

        if (dto.getNome() != null) idosoEncontrado.setNome(dto.getNome());
        if (dto.getTelefone() != null) idosoEncontrado.setTelefone(dto.getTelefone());
        if (dto.getTelegramChatId() != null) idosoEncontrado.setTelegramChatId(dto.getTelegramChatId());
        if (dto.getEmail() != null && !dto.getEmail().isBlank()) idosoEncontrado.setEmail(dto.getEmail());
        if (dto.getSenha() != null && !dto.getSenha().isBlank()) idosoEncontrado.setSenha(dto.getSenha());

        if (dto.getIdFamiliar() != null) {
            Familiar familiar = familiarRepository.findById(dto.getIdFamiliar())
                    .orElseThrow(() -> new RuntimeException("Familiar não encontrado com ID: " + dto.getIdFamiliar()));
            idosoEncontrado.setFamiliar(familiar);
        }

        return idosoRepository.save(idosoEncontrado);
    }

    @Transactional
    public void remover(Long id) {
        if (!idosoRepository.existsById(id)) {
            throw new RuntimeException("Idoso não encontrado com ID: " + id);
        }
        idosoRepository.deleteById(id);
    }
}