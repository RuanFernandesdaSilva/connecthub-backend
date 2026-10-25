package com.auth.simpleauth.repository;

import com.auth.simpleauth.entity.Familiar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FamiliarRepository extends JpaRepository<Familiar, Long> {

    Optional<Familiar> findByEmail(String email);

    @Query("SELECT f FROM Familiar f LEFT JOIN FETCH f.idosos WHERE f.id = :id")
    Optional<Familiar> findByIdWithIdosos(@Param("id") Long id);
}