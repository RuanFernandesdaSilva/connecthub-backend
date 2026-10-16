package com.auth.simpleauth.dto;

public class UsuarioVinculadoDto {
    private Long id;
    private String nome;
    private String email;
    private String fotoUrl;

    public UsuarioVinculadoDto() {}

    public UsuarioVinculadoDto(Long id, String nome, String email, String fotoUrl) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.fotoUrl = fotoUrl;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }
}