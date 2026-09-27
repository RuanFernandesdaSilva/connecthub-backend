package com.auth.simpleauth.dto;

import jakarta.validation.constraints.NotNull;

public class VinculoFamiliarDto {
    @NotNull(message = "O ID do familiar é obrigatório.")
    private Long idFamiliar;

    @NotNull(message = "O ID do idoso é obrigatório.")
    private Long idIdoso;

    public VinculoFamiliarDto() {}

    public VinculoFamiliarDto(Long idFamiliar, Long idIdoso) {
        this.idFamiliar = idFamiliar;
        this.idIdoso = idIdoso;
    }

    public Long getIdFamiliar() { return idFamiliar; }
    public void setIdFamiliar(Long idFamiliar) { this.idFamiliar = idFamiliar; }

    public Long getIdIdoso() { return idIdoso; }
    public void setIdIdoso(Long idIdoso) { this.idIdoso = idIdoso; }
}