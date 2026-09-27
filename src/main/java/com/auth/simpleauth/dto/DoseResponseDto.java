package com.auth.simpleauth.dto;

import com.auth.simpleauth.entity.Dose;
import com.auth.simpleauth.enums.StatusDose;
import com.auth.simpleauth.enums.UnidadeMedida;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;

public class DoseResponseDto {
    private Long id;
    private Long idMedicamento;
    private String nomeMedicamento;
    private Double dosagem;
    private UnidadeMedida unidade;
    private Double quantidadePorDose;
    private LocalDateTime horarioProgramado;
    private LocalDateTime horarioConfirmado;
    private StatusDose status;
    private Long idIdoso;
    private String nomeIdoso;
    private long millisAteHorarioProgramado;


    public DoseResponseDto(Dose dose, Clock clock) {
        this.id = dose.getId();
        this.horarioProgramado = dose.getHorarioProgramado();
        this.horarioConfirmado = dose.getHorarioConfirmado();
        this.status = dose.getStatus();

        if (dose.getMedicamento() != null) {
            this.idMedicamento = dose.getMedicamento().getId();
            this.nomeMedicamento = dose.getMedicamento().getNome();
            this.dosagem = dose.getMedicamento().getDosagem();
            this.unidade = dose.getMedicamento().getUnidade();
            this.quantidadePorDose = dose.getMedicamento().getQuantidadePorDose();

            if (dose.getMedicamento().getIdoso() != null) {
                this.idIdoso = dose.getMedicamento().getIdoso().getId();
                this.nomeIdoso = dose.getMedicamento().getIdoso().getNome();
            }
        }

        if (dose.getHorarioProgramado() != null && clock != null) {

            long targetMillis = dose.getHorarioProgramado().atZone(clock.getZone()).toInstant().toEpochMilli();
            long nowMillis = Instant.now(clock).toEpochMilli();
            this.millisAteHorarioProgramado = Math.max(0, targetMillis - nowMillis);
        }
    }


    public DoseResponseDto(Dose dose) {
        this(dose, Clock.systemDefaultZone());
    }

    public Long getId() { return id; }
    public Long getIdMedicamento() { return idMedicamento; }
    public String getNomeMedicamento() { return nomeMedicamento; }
    public Double getDosagem() { return dosagem; }
    public UnidadeMedida getUnidade() { return unidade; }
    public Double getQuantidadePorDose() { return quantidadePorDose; }
    public LocalDateTime getHorarioProgramado() { return horarioProgramado; }
    public LocalDateTime getHorarioConfirmado() { return horarioConfirmado; }
    public StatusDose getStatus() { return status; }
    public Long getIdIdoso() { return idIdoso; }
    public String getNomeIdoso() { return nomeIdoso; }
    public long getMillisAteHorarioProgramado() { return millisAteHorarioProgramado; }
}