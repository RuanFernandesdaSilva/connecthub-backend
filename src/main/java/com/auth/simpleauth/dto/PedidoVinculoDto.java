package com.auth.simpleauth.dto;

import java.time.LocalDateTime;

public class PedidoVinculoDto {
    private Long idVinculo;
    private Long idFamiliar;
    private String nomeFamiliar;
    private String emailFamiliar;
    private String fotoFamiliarUrl;
    private LocalDateTime dataSolicitacao;

    public PedidoVinculoDto() {}

    public PedidoVinculoDto(Long idVinculo, Long idFamiliar, String nomeFamiliar, String emailFamiliar, String fotoFamiliarUrl, LocalDateTime dataSolicitacao) {
        this.idVinculo = idVinculo;
        this.idFamiliar = idFamiliar;
        this.nomeFamiliar = nomeFamiliar;
        this.emailFamiliar = emailFamiliar;
        this.fotoFamiliarUrl = fotoFamiliarUrl;
        this.dataSolicitacao = dataSolicitacao;
    }

    public Long getIdVinculo() { return idVinculo; }
    public void setIdVinculo(Long idVinculo) { this.idVinculo = idVinculo; }

    public Long getIdFamiliar() { return idFamiliar; }
    public void setIdFamiliar(Long idFamiliar) { this.idFamiliar = idFamiliar; }

    public String getNomeFamiliar() { return nomeFamiliar; }
    public void setNomeFamiliar(String nomeFamiliar) { this.nomeFamiliar = nomeFamiliar; }

    public String getEmailFamiliar() { return emailFamiliar; }
    public void setEmailFamiliar(String emailFamiliar) { this.emailFamiliar = emailFamiliar; }

    public String getFotoFamiliarUrl() { return fotoFamiliarUrl; }
    public void setFotoFamiliarUrl(String fotoFamiliarUrl) { this.fotoFamiliarUrl = fotoFamiliarUrl; }

    public LocalDateTime getDataSolicitacao() { return dataSolicitacao; }
    public void setDataSolicitacao(LocalDateTime dataSolicitacao) { this.dataSolicitacao = dataSolicitacao; }
}