package com.auth.simpleauth.dto;

import com.auth.simpleauth.entity.Idoso;

public class IdosoResponseDto {
    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private String telegramChatId;
    private Long idFamiliar;
    private String nomeFamiliar;

    public IdosoResponseDto(Idoso idoso) {
        this.id = idoso.getId();
        this.nome = idoso.getNome();
        this.email = idoso.getEmail();
        this.telefone = idoso.getTelefone();
        this.telegramChatId = idoso.getTelegramChatId();

        if (idoso.getFamiliar() != null) {
            this.idFamiliar = idoso.getFamiliar().getId();
            this.nomeFamiliar = idoso.getFamiliar().getNome();
        }
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getTelefone() { return telefone; }
    public String getTelegramChatId() { return telegramChatId; }
    public Long getIdFamiliar() { return idFamiliar; }
    public String getNomeFamiliar() { return nomeFamiliar; }
}