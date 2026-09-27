package com.auth.simpleauth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class VinculoTelegramDto {

    @NotNull(message = "O ID do usuário é obrigatório.")
    private Long usuarioId;

    @NotBlank(message = "O Telegram Chat ID é obrigatório.")
    private String telegramChatId;

    public VinculoTelegramDto() {}

    public VinculoTelegramDto(Long usuarioId, String telegramChatId) {
        this.usuarioId = usuarioId;
        this.telegramChatId = telegramChatId;
    }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public String getTelegramChatId() { return telegramChatId; }
    public void setTelegramChatId(String telegramChatId) { this.telegramChatId = telegramChatId; }
}