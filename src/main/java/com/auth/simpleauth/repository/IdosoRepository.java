package com.auth.simpleauth.repository;

import com.auth.simpleauth.entity.Idoso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IdosoRepository extends JpaRepository<Idoso, Long> {
    Optional<Idoso> findByEmail(String email);
}