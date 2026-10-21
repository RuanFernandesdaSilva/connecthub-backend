package com.auth.simpleauth.dto;

import jakarta.validation.constraints.NotNull;

public class RespostaPedidoDto {

    @NotNull(message = "O ID do vínculo é obrigatório.")
    private Long vinculoId;

    private boolean aceito;

    public RespostaPedidoDto() {}

    public RespostaPedidoDto(Long vinculoId, boolean aceito) {
        this.vinculoId = vinculoId;
        this.aceito = aceito;
    }

    public Long getVinculoId() { return vinculoId; }
    public void setVinculoId(Long vinculoId) { this.vinculoId = vinculoId; }

    public boolean isAceito() { return aceito; }
    public void setAceito(boolean aceito) { this.aceito = aceito; }
}