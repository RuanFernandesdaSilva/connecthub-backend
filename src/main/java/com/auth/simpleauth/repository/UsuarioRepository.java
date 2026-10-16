package com.auth.simpleauth.repository;

import com.auth.simpleauth.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmailAndSenha(String email, String senha);
    Optional<Usuario> findByEmail(String email);

    @Modifying
    @Query("UPDATE Usuario u SET u.imagemUrl = :imagemUrl WHERE u.id = :id")
    void atualizarApenasFoto(@Param("id") Long id, @Param("imagemUrl") String imagemUrl);

    @Modifying
    @Query("UPDATE Usuario u SET u.nome = :nome WHERE u.id = :id")
    void atualizarApenasNome(@Param("id") Long id, @Param("nome") String nome);

    @Modifying
    @Query("UPDATE Usuario u SET u.email = :email WHERE u.id = :id")
    void atualizarApenasEmail(@Param("id") Long id, @Param("email") String email);

    @Modifying
    @Query("UPDATE Usuario u SET u.telefone = :telefone WHERE u.id = :id")
    void atualizarApenasTelefone(@Param("id") Long id, @Param("telefone") String telefone);

    // Desconecta o Telegram limpando o telegramChatId e sincroniza a sessão do Hibernate antes da exclusão
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Usuario u SET u.telegramChatId = NULL WHERE u.id = :id")
    void limparTelegramChatId(@Param("id") Long id);
}