package com.auth.simpleauth.repository;

import com.auth.simpleauth.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    public Optional<Usuario> findByEmailAndSenha(String email, String senha);
    public Optional<Usuario> findByEmail(String email);
}
