package com.auth.simpleauth.repository;

import com.auth.simpleauth.entity.Dose;
import com.auth.simpleauth.enums.StatusDose;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DoseRepository extends JpaRepository<Dose, Long> {

    List<Dose> findByMedicamentoId(Long medicamentoId);

    List<Dose> findByMedicamentoIdosoId(Long idosoId);

    @Query("SELECT d FROM Dose d " +
            "LEFT JOIN FETCH d.medicamento m " +
            "LEFT JOIN FETCH m.idoso i " +
            "LEFT JOIN FETCH i.familiar f " +
            "WHERE d.status IN :status AND d.horarioProgramado <= :horario")
    List<Dose> findByStatusInAndHorarioProgramadoLessThanEqual(
            @Param("status") List<StatusDose> status,
            @Param("horario") LocalDateTime horario);

    @Query("SELECT d FROM Dose d " +
            "LEFT JOIN FETCH d.medicamento m " +
            "LEFT JOIN FETCH m.idoso i " +
            "LEFT JOIN FETCH i.familiar f " +
            "WHERE d.id = :id")
    Optional<Dose> findByIdWithFullTree(@Param("id") Long id);

    List<Dose> findByMedicamentoIdosoIdAndStatusOrderByHorarioConfirmadoDesc(Long idosoId, StatusDose status);

    List<Dose> findByMedicamentoIdosoIdAndHorarioProgramadoBetween(Long idosoId, LocalDateTime inicio, LocalDateTime fim);

    @Modifying
    @Transactional
    @Query("UPDATE Dose d SET d.status = :novoStatus, d.horarioConfirmado = :dataHora, d.proximaNotificacao = null WHERE d.id = :doseId AND d.status != :statusTomado")
    int atualizarStatusSePendente(@Param("doseId") Long doseId,
                                  @Param("novoStatus") StatusDose novoStatus,
                                  @Param("dataHora") LocalDateTime dataHora,
                                  @Param("statusTomado") StatusDose statusTomado);
}