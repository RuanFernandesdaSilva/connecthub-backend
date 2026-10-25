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

        // 1. Tenta buscar pelo idFamiliar enviado no DTO
        if (dto.getIdFamiliar() != null) {
            familiar = familiarRepository.findById(dto.getIdFamiliar()).orElse(null);
        }

        // 2. Fallback defensivo: se não foi informado ou não encontrou, associa ao último ou primeiro familiar cadastrado
        if (familiar == null) {
            List<Familiar> familiares = familiarRepository.findAll();
            if (!familiares.isEmpty()) {
                // Pega o último familiar cadastrado (mais recente)
                familiar = familiares.get(familiares.size() - 1);
                System.out.println(">>> ⚠️ [IdosoService] idFamiliar nulo ou não encontrado no DTO ao cadastrar. Associando automaticamente ao Familiar: "
                        + familiar.getNome() + " (ID: " + familiar.getId() + ")");
            }
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

        // 1. Se veio idFamiliar no DTO, atualiza de forma idempotente
        if (dto.getIdFamiliar() != null) {
            boolean jaPossuiMesmoFamiliar = idosoEncontrado.getFamiliar() != null
                    && idosoEncontrado.getFamiliar().getId().equals(dto.getIdFamiliar());

            if (!jaPossuiMesmoFamiliar) {
                Familiar novoFamiliar = familiarRepository.findById(dto.getIdFamiliar())
                        .orElseThrow(() -> new RuntimeException("Familiar não encontrado com ID: " + dto.getIdFamiliar()));
                idosoEncontrado.setFamiliar(novoFamiliar);
            }
        }
        // 2. Fallback defensivo na atualização: se o idoso está sem familiar e não veio idFamiliar no DTO, associa ao familiar mais recente
        else if (idosoEncontrado.getFamiliar() == null) {
            List<Familiar> familiares = familiarRepository.findAll();
            if (!familiares.isEmpty()) {
                Familiar familiarFallback = familiares.get(familiares.size() - 1);
                idosoEncontrado.setFamiliar(familiarFallback);
                System.out.println(">>> ⚠️ [IdosoService] Idoso ID: " + id + " estava sem familiar. Associado automaticamente ao Familiar: "
                        + familiarFallback.getNome() + " (ID: " + familiarFallback.getId() + ")");
            }
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

    public List<Familiar> listarFamiliares() {
        return familiarRepository.findAll();
    }
}