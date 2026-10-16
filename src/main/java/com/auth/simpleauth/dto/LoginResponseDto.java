package com.auth.simpleauth.dto;

import com.auth.simpleauth.entity.Familiar;
import com.auth.simpleauth.entity.Idoso;
import com.auth.simpleauth.entity.Usuario;

public class LoginResponseDto {

    private Long id;
    private String nome;
    private String email;
    private String tipo;
    private boolean telegramConectado;

    public LoginResponseDto(Usuario usuario) {
        this.id = usuario.getId();
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();

        boolean chatIdValido = usuario.getTelegramChatId() != null && !usuario.getTelegramChatId().isBlank();

        if (usuario instanceof Idoso idoso) {
            this.tipo = "IDOSO";
            this.telegramConectado = chatIdValido && idoso.isTodosBotsConectados();
        } else if (usuario instanceof Familiar familiar) {
            this.tipo = "FAMILIAR";
            this.telegramConectado = chatIdValido && familiar.isBotMedicamentosConectado();
        }
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getTipo() { return tipo; }
    public boolean isTelegramConectado() { return telegramConectado; }
}