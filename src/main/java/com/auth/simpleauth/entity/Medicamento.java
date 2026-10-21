package com.auth.simpleauth.entity;

import com.auth.simpleauth.enums.TipoTratamento;
import com.auth.simpleauth.enums.UnidadeMedida;
import com.auth.simpleauth.enums.UnidadeTempoDuracao;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_medicamento")
public class Medicamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private Double dosagem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UnidadeMedida unidade;

    @Column(nullable = false)
    private Double quantidadePorDose;

    @Column(nullable = false)
    private LocalDateTime primeiroHorario;

    @Column(nullable = false)
    private Integer intervaloHoras;

    @Column(nullable = false)
    private Boolean ativo = true;


    private String corCaixa;
    private String identificadorCaixa;


    @Enumerated(EnumType.STRING)
    private TipoTratamento tipoTratamento;

    @Enumerated(EnumType.STRING)
    private UnidadeTempoDuracao unidadeDuracao;

    private Integer valorDuracao;

    private LocalDateTime dataFimTratamento;

    @ManyToOne
    @JoinColumn(name = "id_idoso", nullable = false)
    private Idoso idoso;

    @ManyToOne
    @JoinColumn(name = "id_familiar")
    private Familiar familiar;

    public Medicamento() {}

    public Medicamento(String nome, Double dosagem, UnidadeMedida unidade, Double quantidadePorDose,
                       LocalDateTime primeiroHorario, Integer intervaloHoras, String corCaixa,
                       String identificadorCaixa, TipoTratamento tipoTratamento,
                       UnidadeTempoDuracao unidadeDuracao, Integer valorDuracao,
                       LocalDateTime dataFimTratamento, Idoso idoso, Familiar familiar) {
        this.nome = nome;
        this.dosagem = dosagem;
        this.unidade = unidade;
        this.quantidadePorDose = quantidadePorDose;
        this.primeiroHorario = primeiroHorario;
        this.intervaloHoras = intervaloHoras;
        this.corCaixa = corCaixa;
        this.identificadorCaixa = identificadorCaixa;
        this.tipoTratamento = tipoTratamento;
        this.unidadeDuracao = unidadeDuracao;
        this.valorDuracao = valorDuracao;
        this.dataFimTratamento = dataFimTratamento;
        this.idoso = idoso;
        this.familiar = familiar;
        this.ativo = true;
    }

    public TipoTratamento getTipoTratamento() { return tipoTratamento; }
    public void setTipoTratamento(TipoTratamento tipoTratamento) { this.tipoTratamento = tipoTratamento; }

    public UnidadeTempoDuracao getUnidadeDuracao() { return unidadeDuracao; }
    public void setUnidadeDuracao(UnidadeTempoDuracao unidadeDuracao) { this.unidadeDuracao = unidadeDuracao; }

    public Integer getValorDuracao() { return valorDuracao; }
    public void setValorDuracao(Integer valorDuracao) { this.valorDuracao = valorDuracao; }

    public LocalDateTime getDataFimTratamento() { return dataFimTratamento; }
    public void setDataFimTratamento(LocalDateTime dataFimTratamento) { this.dataFimTratamento = dataFimTratamento; }

    public String getCorCaixa() { return corCaixa; }
    public void setCorCaixa(String corCaixa) { this.corCaixa = corCaixa; }

    public String getIdentificadorCaixa() { return identificadorCaixa; }
    public void setIdentificadorCaixa(String identificadorCaixa) { this.identificadorCaixa = identificadorCaixa; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    public Idoso getIdoso() { return idoso; }
    public void setIdoso(Idoso idoso) { this.idoso = idoso; }

    public Familiar getFamiliar() { return familiar; }
    public void setFamiliar(Familiar familiar) { this.familiar = familiar; }
}