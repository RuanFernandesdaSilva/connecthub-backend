package com.auth.simpleauth.dto;

public class VinculoDetalhadoResponseDto {
    private String nomeFamiliar;
    private String nomeIdoso;
    private long totalIdososDoFamiliar;
    private long totalFamiliaresDoIdoso;

    public VinculoDetalhadoResponseDto() {}

    public VinculoDetalhadoResponseDto(String nomeFamiliar, String nomeIdoso, long totalIdososDoFamiliar, long totalFamiliaresDoIdoso) {
        this.nomeFamiliar = nomeFamiliar;
        this.nomeIdoso = nomeIdoso;
        this.totalIdososDoFamiliar = totalIdososDoFamiliar;
        this.totalFamiliaresDoIdoso = totalFamiliaresDoIdoso;
    }

    public String getNomeFamiliar() { return nomeFamiliar; }
    public void setNomeFamiliar(String nomeFamiliar) { this.nomeFamiliar = nomeFamiliar; }

    public String getNomeIdoso() { return nomeIdoso; }
    public void setNomeIdoso(String nomeIdoso) { this.nomeIdoso = nomeIdoso; }

    public long getTotalIdososDoFamiliar() { return totalIdososDoFamiliar; }
    public void setTotalIdososDoFamiliar(long totalIdososDoFamiliar) { this.totalIdososDoFamiliar = totalIdososDoFamiliar; }

    public long getTotalFamiliaresDoIdoso() { return totalFamiliaresDoIdoso; }
    public void setTotalFamiliaresDoIdoso(long totalFamiliaresDoIdoso) { this.totalFamiliaresDoIdoso = totalFamiliaresDoIdoso; }
}