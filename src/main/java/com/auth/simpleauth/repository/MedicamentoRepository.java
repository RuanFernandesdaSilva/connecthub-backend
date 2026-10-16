package com.auth.simpleauth.repository;

import com.auth.simpleauth.entity.Medicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MedicamentoRepository extends JpaRepository<Medicamento, Long> {

    List<Medicamento> findByIdosoIdAndAtivoTrue(Long idosoId);


    List<Medicamento> findByAtivoTrueAndDataFimTratamentoLessThanEqual(LocalDateTime dataHora);
}