package com.auth.simpleauth.dto;

import com.auth.simpleauth.enums.Perfil;

public class UsuarioPerfilDto {

    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private Perfil perfil;
    private String imagemUrl;

    public UsuarioPerfilDto(
            Long id,
            String nome,
            String email,
            String telefone,
            Perfil perfil,
            String imagemUrl
    ) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.perfil = perfil;
        this.imagemUrl = imagemUrl;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefone() {
        return telefone;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public String getImagemUrl() {
        return imagemUrl;
    }
}