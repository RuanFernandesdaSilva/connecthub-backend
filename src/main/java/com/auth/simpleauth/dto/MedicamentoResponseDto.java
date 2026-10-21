package com.auth.simpleauth.dto;

import com.auth.simpleauth.entity.Medicamento;
import com.auth.simpleauth.enums.TipoTratamento;
import com.auth.simpleauth.enums.UnidadeMedida;
import com.auth.simpleauth.enums.UnidadeTempoDuracao;
import java.time.LocalDateTime;

public class MedicamentoResponseDto {
    private Long id;
    private String nome;
    private Double dosagem;
    private UnidadeMedida unidade;
    private Double quantidadePorDose;
    private LocalDateTime primeiroHorario;
    private Integer intervaloHoras;
    private Boolean ativo;


    private String corCaixa;
    private String identificadorCaixa;


    private TipoTratamento tipoTratamento;
    private UnidadeTempoDuracao unidadeDuracao;
    private Integer valorDuracao;
    private LocalDateTime dataFimTratamento;

    private Long idIdoso;
    private String nomeIdoso;

    public MedicamentoResponseDto(Medicamento medicamento) {
        this.id = medicamento.getId();
        this.nome = medicamento.getNome();
        this.dosagem = medicamento.getDosagem();
        this.unidade = medicamento.getUnidade();
        this.quantidadePorDose = medicamento.getQuantidadePorDose();
        this.primeiroHorario = medicamento.getPrimeiroHorario();
        this.intervaloHoras = medicamento.getIntervaloHoras();
        this.ativo = medicamento.getAtivo();
        this.corCaixa = medicamento.getCorCaixa();
        this.identificadorCaixa = medicamento.getIdentificadorCaixa();
        this.tipoTratamento = medicamento.getTipoTratamento();
        this.unidadeDuracao = medicamento.getUnidadeDuracao();
        this.valorDuracao = medicamento.getValorDuracao();
        this.dataFimTratamento = medicamento.getDataFimTratamento();

        if (medicamento.getIdoso() != null) {
            this.idIdoso = medicamento.getIdoso().getId();
            this.nomeIdoso = medicamento.getIdoso().getNome();
        }
    }


    public Long getId() { return id; }
    public String getNome() { return nome; }
    public Double getDosagem() { return dosagem; }
    public UnidadeMedida getUnidade() { return unidade; }
    public Double getQuantidadePorDose() { return quantidadePorDose; }
    public LocalDateTime getPrimeiroHorario() { return primeiroHorario; }
    public Integer getIntervaloHoras() { return intervaloHoras; }
    public Boolean getAtivo() { return ativo; }
    public String getCorCaixa() { return corCaixa; }
    public String getIdentificadorCaixa() { return identificadorCaixa; }
    public TipoTratamento getTipoTratamento() { return tipoTratamento; }
    public UnidadeTempoDuracao getUnidadeDuracao() { return unidadeDuracao; }
    public Integer getValorDuracao() { return valorDuracao; }
    public LocalDateTime getDataFimTratamento() { return dataFimTratamento; }
    public Long getIdIdoso() { return idIdoso; }
    public String getNomeIdoso() { return nomeIdoso; }
}