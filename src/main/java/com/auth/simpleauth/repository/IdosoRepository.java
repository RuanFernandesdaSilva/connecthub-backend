package com.auth.simpleauth.repository;

import com.auth.simpleauth.entity.Idoso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IdosoRepository extends JpaRepository<Idoso, Long> {

    Optional<Idoso> findByEmail(String email);

    @Query("SELECT i FROM Idoso i LEFT JOIN FETCH i.familiar WHERE i.id = :id")
    Optional<Idoso> findByIdWithFamiliar(@Param("id") Long id);
}