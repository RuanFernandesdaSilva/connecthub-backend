package com.auth.simpleauth.dto;

import com.auth.simpleauth.entity.Familiar;
import com.auth.simpleauth.enums.Perfil;

public class FamiliarResponseDto {
    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private String telegramChatId;
    private Perfil perfil;

    public FamiliarResponseDto(Familiar familiar) {
        this.id = familiar.getId();
        this.nome = familiar.getNome();
        this.email = familiar.getEmail();
        this.telefone = familiar.getTelefone();
        this.telegramChatId = familiar.getTelegramChatId();
        this.perfil = familiar.getPerfil();
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getTelefone() { return telefone; }
    public String getTelegramChatId() { return telegramChatId; }
    public Perfil getPerfil() { return perfil; }
}