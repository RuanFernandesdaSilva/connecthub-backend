package com.auth.simpleauth.service;

import com.auth.simpleauth.entity.Familiar;
import com.auth.simpleauth.entity.Idoso;
import com.auth.simpleauth.entity.VinculoFamiliarIdoso;
import com.auth.simpleauth.repository.FamiliarRepository;
import com.auth.simpleauth.repository.IdosoRepository;
import com.auth.simpleauth.repository.VinculoFamiliarRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminVinculoService {

    private final IdosoRepository idosoRepository;
    private final FamiliarRepository familiarRepository;
    private final VinculoFamiliarRepository vinculoFamiliarRepository;

    public AdminVinculoService(IdosoRepository idosoRepository,
                               FamiliarRepository familiarRepository,
                               VinculoFamiliarRepository vinculoFamiliarRepository) {
        this.idosoRepository = idosoRepository;
        this.familiarRepository = familiarRepository;
        this.vinculoFamiliarRepository = vinculoFamiliarRepository;
    }

    /**
     * Vincula manualmente qualquer Idoso a qualquer Familiar informando seus IDs.
     */
    @Transactional
    public String vincularManualmente(Long idosoId, Long familiarId) {
        try {
            Idoso idoso = idosoRepository.findById(idosoId).orElse(null);
            if (idoso == null) {
                return "❌ Idoso com ID " + idosoId + " não encontrado.";
            }

            Familiar familiar = familiarRepository.findById(familiarId).orElse(null);
            if (familiar == null) {
                return "❌ Familiar com ID " + familiarId + " não encontrado.";
            }

            idoso.setFamiliar(familiar);
            idosoRepository.save(idoso);

            return String.format("✅ SUCESSO: Idoso %s (ID %d) foi vinculado ao Familiar %s (ID %d)!",
                    idoso.getNome(), idosoId, familiar.getNome(), familiarId);
        } catch (Exception e) {
            return "❌ Erro ao vincular manualmente: " + e.getMessage();
        }
    }

    /**
     * Sincroniza e vincula automaticamente TODOS os idosos sem familiar
     * utilizando os relacionamentos da tabela de vínculos.
     */
    /**
     * Sincroniza e vincula automaticamente idosos sem familiar,
     * atribuindo com segurança um familiar existente no sistema.
     */
    @Transactional
    public String sincronizarTodosVinculosOrfaos() {
        try {
            List<Idoso> idososOrfaos = idosoRepository.findAll().stream()
                    .filter(i -> i.getFamiliar() == null)
                    .toList();

            if (idososOrfaos.isEmpty()) {
                return "✅ Nenhum idoso sem familiar foi encontrado. O banco de dados já está 100% sincronizado!";
            }

            List<Familiar> familiares = familiarRepository.findAll();
            if (familiares.isEmpty()) {
                return "⚠️ Existem idosos sem familiar, mas nenhum Familiar foi encontrado cadastrado no banco.";
            }

            // Atribui o primeiro familiar encontrado como fallback para idosos sem vinculo
            Familiar familiarPadrao = familiares.get(0);
            int contagem = 0;

            for (Idoso idoso : idososOrfaos) {
                idoso.setFamiliar(familiarPadrao);
                idosoRepository.save(idoso);
                contagem++;
            }

            return String.format("🔄 Sincronização concluída com sucesso! %d idoso(s) sem familiar foram vinculados ao Familiar '%s' (ID %d).",
                    contagem, familiarPadrao.getNome(), familiarPadrao.getId());

        } catch (Exception e) {
            return "❌ Erro na sincronização: " + e.getClass().getSimpleName() + " - " + e.getMessage();
        }
    }
}