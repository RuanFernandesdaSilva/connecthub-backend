package com.auth.simpleauth.dto;

import com.auth.simpleauth.entity.Familiar;
import com.auth.simpleauth.entity.Idoso;

public class IdosoDto {
    private String nome;
    private String email;
    private String senha;
    private String telefone;
    private String telegramChatId;
    private Long idFamiliar;

    public IdosoDto() {}

    public IdosoDto(String nome, String email, String senha, String telefone, String telegramChatId, Long idFamiliar) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.telefone = telefone;
        this.telegramChatId = telegramChatId;
        this.idFamiliar = idFamiliar;
    }


    public Idoso toEntity(Familiar familiar) {
        String senhaDefinida = (this.senha != null && !this.senha.isBlank()) ? this.senha : "123456";

        String emailDefinido = (this.email != null && !this.email.isBlank())
                ? this.email
                : "idoso_" + System.currentTimeMillis() + "@email.com";

        return new Idoso(
                this.nome,
                emailDefinido,
                senhaDefinida,
                this.telefone,
                this.telegramChatId,
                familiar
        );
    }


    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getTelegramChatId() { return telegramChatId; }
    public void setTelegramChatId(String telegramChatId) { this.telegramChatId = telegramChatId; }

    public Long getIdFamiliar() { return idFamiliar; }
    public void setIdFamiliar(Long idFamiliar) { this.idFamiliar = idFamiliar; }
}