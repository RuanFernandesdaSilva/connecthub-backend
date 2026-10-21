package com.auth.simpleauth.entity;

import com.auth.simpleauth.enums.StatusVinculo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "tbl_vinculo_familiar_idoso",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_familiar_idoso", columnNames = {"familiar_id", "idoso_id"})
        },
        indexes = {
                @Index(name = "idx_vinculo_idoso_status", columnList = "idoso_id, status"),
                @Index(name = "idx_vinculo_familiar_status", columnList = "familiar_id, status")
        }
)
public class VinculoFamiliarIdoso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "familiar_id", nullable = false)
    private Familiar familiar;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idoso_id", nullable = false)
    private Idoso idoso;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusVinculo status = StatusVinculo.PENDENTE;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    public VinculoFamiliarIdoso() {}

    public VinculoFamiliarIdoso(Familiar familiar, Idoso idoso) {
        this.familiar = familiar;
        this.idoso = idoso;
        this.status = StatusVinculo.PENDENTE;
    }

    public VinculoFamiliarIdoso(Familiar familiar, Idoso idoso, StatusVinculo status) {
        this.familiar = familiar;
        this.idoso = idoso;
        this.status = status;
    }

    @PrePersist
    protected void onCreate() {
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.dataAtualizacao = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Familiar getFamiliar() { return familiar; }
    public void setFamiliar(Familiar familiar) { this.familiar = familiar; }

    public Idoso getIdoso() { return idoso; }
    public void setIdoso(Idoso idoso) { this.idoso = idoso; }

    public StatusVinculo getStatus() { return status; }
    public void setStatus(StatusVinculo status) { this.status = status; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
}