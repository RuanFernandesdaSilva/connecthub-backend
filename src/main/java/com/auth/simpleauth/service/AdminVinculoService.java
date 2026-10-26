package com.auth.simpleauth.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        try {
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
        } catch (Exception e) {
            return "❌ Erro ao vincular manualmente: " + e.getMessage();
        }
    }

    /**
     * Sincroniza e vincula automaticamente TODOS os idosos que estão sem familiar_id,
     * utilizando os relacionamentos da tabela associativa.
     */
    @Transactional
    public String sincronizarTodosVinculosOrfaos() {
        try {
            // SQL seguro para PostgreSQL utilizando subquery para evitar erros de sintaxe JOIN/UPDATE
            String sqlQuery =
                    "UPDATE idoso " +
                            "SET familiar_id = (" +
                            "    SELECT v.familiar_id " +
                            "    FROM tbl_vinculo_familiar_idoso v " +
                            "    WHERE v.idoso_id = idoso.id " +
                            "    ORDER BY v.id DESC LIMIT 1" +
                            ") " +
                            "WHERE familiar_id IS NULL " +
                            "  AND EXISTS (" +
                            "    SELECT 1 FROM tbl_vinculo_familiar_idoso v WHERE v.idoso_id = idoso.id" +
                            ")";

            int atualizados = jdbcTemplate.update(sqlQuery);

            return String.format("🔄 Sincronização concluída! %d idoso(s) sem familiar foram vinculados automaticamente.", atualizados);
        } catch (Exception e) {
            return "❌ Erro na sincronização: " + e.getMessage();
        }
    }
}