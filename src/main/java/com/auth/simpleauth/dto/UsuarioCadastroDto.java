package com.auth.simpleauth.dto;

import com.auth.simpleauth.entity.Familiar;
import com.auth.simpleauth.entity.Idoso;
import com.auth.simpleauth.entity.Usuario;
import com.auth.simpleauth.enums.Perfil;

public class UsuarioCadastroDto {
    private String nome;
    private String email;
    private String senha;
    private String telefone;
    private String telegramChatId;
    private Perfil perfil;

    public UsuarioCadastroDto() {}

    public UsuarioCadastroDto(String nome, String email, String senha, String telefone, String telegramChatId, Perfil perfil) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.telefone = telefone;
        this.telegramChatId = telegramChatId;
        this.perfil = perfil;
    }


    public Usuario toEntity() {
        if (this.perfil == Perfil.FAMILIAR) {
            return new Familiar(this.nome, this.email, this.senha, this.telefone, this.telegramChatId);
        } else if (this.perfil == Perfil.IDOSO) {
            return new Idoso(this.nome, this.email, this.senha, this.telefone, this.telegramChatId, null);
        }
        throw new IllegalArgumentException("Perfil de usuário inválido: " + this.perfil);
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

    public Perfil getPerfil() { return perfil; }
    public void setPerfil(Perfil perfil) { this.perfil = perfil; }
}