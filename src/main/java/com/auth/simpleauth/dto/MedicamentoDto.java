package com.auth.simpleauth.dto;

import com.auth.simpleauth.enums.TipoTratamento;
import com.auth.simpleauth.enums.UnidadeMedida;
import com.auth.simpleauth.enums.UnidadeTempoDuracao;
import java.time.LocalDateTime;

public class MedicamentoDto {
    private String nome;
    private Double dosagem;
    private UnidadeMedida unidade;
    private Double quantidadePorDose;
    private LocalDateTime primeiroHorario;
    private Integer intervaloHoras;
    private Long idIdoso;


    private String corCaixa;
    private String identificadorCaixa;


    private TipoTratamento tipoTratamento;
    private UnidadeTempoDuracao unidadeDuracao;
    private Integer valorDuracao;

    public MedicamentoDto() {}

    public MedicamentoDto(String nome, Double dosagem, UnidadeMedida unidade, Double quantidadePorDose,
                          LocalDateTime primeiroHorario, Integer intervaloHoras, Long idIdoso,
                          String corCaixa, String identificadorCaixa, TipoTratamento tipoTratamento,
                          UnidadeTempoDuracao unidadeDuracao, Integer valorDuracao) {
        this.nome = nome;
        this.dosagem = dosagem;
        this.unidade = unidade;
        this.quantidadePorDose = quantidadePorDose;
        this.primeiroHorario = primeiroHorario;
        this.intervaloHoras = intervaloHoras;
        this.idIdoso = idIdoso;
        this.corCaixa = corCaixa;
        this.identificadorCaixa = identificadorCaixa;
        this.tipoTratamento = tipoTratamento;
        this.unidadeDuracao = unidadeDuracao;
        this.valorDuracao = valorDuracao;
    }


    public TipoTratamento getTipoTratamento() { return tipoTratamento; }
    public void setTipoTratamento(TipoTratamento tipoTratamento) { this.tipoTratamento = tipoTratamento; }

    public UnidadeTempoDuracao getUnidadeDuracao() { return unidadeDuracao; }
    public void setUnidadeDuracao(UnidadeTempoDuracao unidadeDuracao) { this.unidadeDuracao = unidadeDuracao; }

    public Integer getValorDuracao() { return valorDuracao; }
    public void setValorDuracao(Integer valorDuracao) { this.valorDuracao = valorDuracao; }


    public String getCorCaixa() { return corCaixa; }
    public void setCorCaixa(String corCaixa) { this.corCaixa = corCaixa; }

    public String getIdentificadorCaixa() { return identificadorCaixa; }
    public void setIdentificadorCaixa(String identificadorCaixa) { this.identificadorCaixa = identificadorCaixa; }


    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Double getDosagem() { return dosagem; }
    public void setDosagem(Double dosagem) { this.dosagem = dosagem; }

    public UnidadeMedida getUnidade() { return unidade; }
    public void setUnidade(UnidadeMedida unidade) { this.unidade = unidade; }

    public Double getQuantidadePorDose() { return quantidadePorDose; }
    public void setQuantidadePorDose(Double quantidadePorDose) { this.quantidadePorDose = quantidadePorDose; }

    public LocalDateTime getPrimeiroHorario() { return primeiroHorario; }
    public void setPrimeiroHorario(LocalDateTime primeiroHorario) { this.primeiroHorario = primeiroHorario; }

    public Integer getIntervaloHoras() { return intervaloHoras; }
    public void setIntervaloHoras(Integer intervaloHoras) { this.intervaloHoras = intervaloHoras; }

    public Long getIdIdoso() { return idIdoso; }
    public void setIdIdoso(Long idIdoso) { this.idIdoso = idIdoso; }
}