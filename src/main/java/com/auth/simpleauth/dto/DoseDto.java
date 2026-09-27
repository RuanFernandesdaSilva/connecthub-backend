package com.auth.simpleauth.dto;

import com.auth.simpleauth.enums.StatusDose;
import java.time.LocalDateTime;

public class DoseDto {

    private Long idMedicamento;
    private LocalDateTime horarioProgramado;
    private StatusDose status;

    public DoseDto() {}

    public DoseDto(Long idMedicamento, LocalDateTime horarioProgramado, StatusDose status) {
        this.idMedicamento = idMedicamento;
        this.horarioProgramado = horarioProgramado;
        this.status = status;
    }

    public Long getIdMedicamento() { return idMedicamento; }
    public void setIdMedicamento(Long idMedicamento) { this.idMedicamento = idMedicamento; }

    public LocalDateTime getHorarioProgramado() { return horarioProgramado; }
    public void setHorarioProgramado(LocalDateTime horarioProgramado) { this.horarioProgramado = horarioProgramado; }

    public StatusDose getStatus() { return status; }
    public void setStatus(StatusDose status) { this.status = status; }
}