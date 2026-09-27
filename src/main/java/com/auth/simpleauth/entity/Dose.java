package com.auth.simpleauth.entity;

import com.auth.simpleauth.enums.StatusDose;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_dose")
public class Dose {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_medicamento", nullable = false)
    private Medicamento medicamento;

    @Column(nullable = false)
    private LocalDateTime horarioProgramado;

    private LocalDateTime horarioConfirmado;

    private LocalDateTime proximaNotificacao;

    @Column(nullable = false)
    private Integer quantidadeNotificacoes = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusDose status = StatusDose.PENDENTE;

    public Dose() {}

    public Dose(Medicamento medicamento, LocalDateTime horarioProgramado) {
        this.medicamento = medicamento;
        this.horarioProgramado = horarioProgramado;
        this.status = StatusDose.PENDENTE;
        this.quantidadeNotificacoes = 0;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Medicamento getMedicamento() { return medicamento; }
    public void setMedicamento(Medicamento medicamento) { this.medicamento = medicamento; }

    public LocalDateTime getHorarioProgramado() { return horarioProgramado; }
    public void setHorarioProgramado(LocalDateTime horarioProgramado) { this.horarioProgramado = horarioProgramado; }

    public LocalDateTime getHorarioConfirmado() { return horarioConfirmado; }
    public void setHorarioConfirmado(LocalDateTime horarioConfirmado) { this.horarioConfirmado = horarioConfirmado; }

    public LocalDateTime getProximaNotificacao() { return proximaNotificacao; }
    public void setProximaNotificacao(LocalDateTime proximaNotificacao) { this.proximaNotificacao = proximaNotificacao; }

    public Integer getQuantidadeNotificacoes() { return quantidadeNotificacoes; }
    public void setQuantidadeNotificacoes(Integer quantidadeNotificacoes) { this.quantidadeNotificacoes = quantidadeNotificacoes; }

    public StatusDose getStatus() { return status; }
    public void setStatus(StatusDose status) { this.status = status; }
}