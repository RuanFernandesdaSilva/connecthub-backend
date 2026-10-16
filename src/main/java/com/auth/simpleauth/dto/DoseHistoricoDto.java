package com.auth.simpleauth.dto;

import com.auth.simpleauth.entity.Dose;
import com.auth.simpleauth.enums.StatusDose;
import com.auth.simpleauth.enums.TipoTratamento;
import com.auth.simpleauth.enums.UnidadeMedida;
import com.auth.simpleauth.enums.UnidadeTempoDuracao;

import java.time.LocalDateTime;

public class DoseHistoricoDto {

    private Long doseId;
    private String nomeMedicamento;
    private Double dosagem;
    private UnidadeMedida unidade;
    private Double quantidadePorDose;
    private LocalDateTime horarioProgramado;
    private LocalDateTime horarioConfirmado;
    private StatusDose status;
    private TipoTratamento tipoTratamento;
    private Integer valorDuracao;
    private UnidadeTempoDuracao unidadeDuracao;
    private LocalDateTime dataFimTratamento;

    public DoseHistoricoDto() {}

    public static DoseHistoricoDto fromEntity(Dose dose) {
        DoseHistoricoDto dto = new DoseHistoricoDto();
        dto.setDoseId(dose.getId());
        dto.setHorarioProgramado(dose.getHorarioProgramado());
        dto.setHorarioConfirmado(dose.getHorarioConfirmado());
        dto.setStatus(dose.getStatus());

        if (dose.getMedicamento() != null) {
            dto.setNomeMedicamento(dose.getMedicamento().getNome());
            dto.setDosagem(dose.getMedicamento().getDosagem());
            dto.setUnidade(dose.getMedicamento().getUnidade());
            dto.setQuantidadePorDose(dose.getMedicamento().getQuantidadePorDose());
            dto.setTipoTratamento(dose.getMedicamento().getTipoTratamento());
            dto.setValorDuracao(dose.getMedicamento().getValorDuracao());
            dto.setUnidadeDuracao(dose.getMedicamento().getUnidadeDuracao());
            dto.setDataFimTratamento(dose.getMedicamento().getDataFimTratamento());
        }

        return dto;
    }

    // Getters e Setters
    public Long getDoseId() { return doseId; }
    public void setDoseId(Long doseId) { this.doseId = doseId; }

    public String getNomeMedicamento() { return nomeMedicamento; }
    public void setNomeMedicamento(String nomeMedicamento) { this.nomeMedicamento = nomeMedicamento; }

    public Double getDosagem() { return dosagem; }
    public void setDosagem(Double dosagem) { this.dosagem = dosagem; }

    public UnidadeMedida getUnidade() { return unidade; }
    public void setUnidade(UnidadeMedida unidade) { this.unidade = unidade; }

    public Double getQuantidadePorDose() { return quantidadePorDose; }
    public void setQuantidadePorDose(Double quantidadePorDose) { this.quantidadePorDose = quantidadePorDose; }

    public LocalDateTime getHorarioProgramado() { return horarioProgramado; }
    public void setHorarioProgramado(LocalDateTime horarioProgramado) { this.horarioProgramado = horarioProgramado; }

    public LocalDateTime getHorarioConfirmado() { return horarioConfirmado; }
    public void setHorarioConfirmado(LocalDateTime horarioConfirmado) { this.horarioConfirmado = horarioConfirmado; }

    public StatusDose getStatus() { return status; }
    public void setStatus(StatusDose status) { this.status = status; }

    public TipoTratamento getTipoTratamento() { return tipoTratamento; }
    public void setTipoTratamento(TipoTratamento tipoTratamento) { this.tipoTratamento = tipoTratamento; }

    public Integer getValorDuracao() { return valorDuracao; }
    public void setValorDuracao(Integer valorDuracao) { this.valorDuracao = valorDuracao; }

    public UnidadeTempoDuracao getUnidadeDuracao() { return unidadeDuracao; }
    public void setUnidadeDuracao(UnidadeTempoDuracao unidadeDuracao) { this.unidadeDuracao = unidadeDuracao; }

    public LocalDateTime getDataFimTratamento() { return dataFimTratamento; }
    public void setDataFimTratamento(LocalDateTime dataFimTratamento) { this.dataFimTratamento = dataFimTratamento; }
}