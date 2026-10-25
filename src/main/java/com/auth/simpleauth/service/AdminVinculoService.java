
package com.auth.simpleauth.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class AdminVinculoService {

    private final JdbcTemplate jdbcTemplate;

    public AdminVinculoService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Vincula manualmente qualquer Idoso a qualquer Familiar informando seus IDs.
     */
    @Transactional
    public String vincularManualmente(Long idosoId, Long familiarId) {
        int linhasIdoso = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM idoso WHERE id = ?", Integer.class, idosoId);
        int linhasFamiliar = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM familiar WHERE id = ?", Integer.class, familiarId);

        if (linhasIdoso == 0) {
            return "❌ Idoso com ID " + idosoId + " não encontrado.";
        }
        if (linhasFamiliar == 0) {
            return "❌ Familiar com ID " + familiarId + " não encontrado.";
        }

        jdbcTemplate.update("UPDATE idoso SET familiar_id = ? WHERE id = ?", familiarId, idosoId);
        return String.format("✅ SUCESSO: Idoso (ID %d) foi vinculado ao Familiar (ID %d)!", idosoId, familiarId);
    }

    /**
     * Sincroniza e vincula automaticamente TODOS os idosos que estão sem familiar_id,
     * utilizando os relacionamentos aceitos na tabela de vinculos.
     */
    @Transactional
    public String sincronizarTodosVinculosOrfaos() {
        // Query que busca vinculos aceitos e atualiza os idosos sem familiar
        String sqlQuery =
                "UPDATE idoso i " +
                        "SET familiar_id = v.familiar_id " +
                        "FROM vinculo v " +
                        "WHERE i.id = v.idoso_id " +
                        "  AND v.status = 'ACEITO' " +
                        "  AND i.familiar_id IS NULL";

        int atualizados = jdbcTemplate.update(sqlQuery);

        return String.format("🔄 Sincronização concluída! %d idoso(s) sem familiar foram vinculados automaticamente.", atualizados);
    }
}
