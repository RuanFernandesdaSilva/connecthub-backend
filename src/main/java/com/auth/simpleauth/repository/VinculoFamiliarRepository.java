package com.auth.simpleauth.repository;

import com.auth.simpleauth.entity.VinculoFamiliarIdoso;
import com.auth.simpleauth.enums.StatusVinculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VinculoFamiliarRepository extends JpaRepository<VinculoFamiliarIdoso, Long> {

    Optional<VinculoFamiliarIdoso> findByFamiliarIdAndIdosoId(Long familiarId, Long idosoId);

    @Query("SELECT v FROM VinculoFamiliarIdoso v JOIN FETCH v.familiar WHERE v.idoso.id = :idosoId AND v.status = :status")
    List<VinculoFamiliarIdoso> findByIdosoIdAndStatusWithFamiliar(@Param("idosoId") Long idosoId, @Param("status") StatusVinculo status);

    @Query("SELECT v FROM VinculoFamiliarIdoso v JOIN FETCH v.idoso WHERE v.familiar.id = :familiarId AND v.status = :status")
    List<VinculoFamiliarIdoso> findByFamiliarIdAndStatusWithIdoso(@Param("familiarId") Long familiarId, @Param("status") StatusVinculo status);

    boolean existsByFamiliarIdAndIdosoIdAndStatus(Long familiarId, Long idosoId, StatusVinculo status);

    long countByFamiliarIdAndStatus(Long familiarId, StatusVinculo status);

    long countByIdosoIdAndStatus(Long idosoId, StatusVinculo status);

    // Remove todos os vínculos e força o Hibernate a sincronizar e limpar o cache da memória
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM VinculoFamiliarIdoso v WHERE v.familiar.id = :familiarId OR v.idoso.id = :idosoId")
    void deleteByFamiliarIdOrIdosoId(@Param("familiarId") Long familiarId, @Param("idosoId") Long idosoId);
}