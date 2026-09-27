package com.auth.simpleauth.dto;

import com.auth.simpleauth.entity.Familiar;

public class FamiliarDto {
    private String nome;
    private String email;
    private String senha;
    private String telefone;
    private String telegramChatId;

    public FamiliarDto() {}

    public FamiliarDto(String nome, String email, String senha, String telefone, String telegramChatId) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.telefone = telefone;
        this.telegramChatId = telegramChatId;
    }

    public Familiar toEntity() {

        String senhaDefinida = (this.senha != null && !this.senha.isBlank()) ? this.senha : "123456";
        return new Familiar(this.nome, this.email, senhaDefinida, this.telefone, this.telegramChatId);
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
}