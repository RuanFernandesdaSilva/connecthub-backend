package com.auth.simpleauth.entity;

import com.auth.simpleauth.enums.Perfil;
import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("FAMILIAR")
public class Familiar extends Usuario {

    @OneToMany(mappedBy = "familiar", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Idoso> idosos = new ArrayList<>();

    @OneToMany(mappedBy = "familiar", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Medicamento> medicamentosCadastrados = new ArrayList<>();

    private Boolean botMedicamentosConectado = false;
    private Boolean botEventosConectado = false;
    private Boolean botComprasConectado = false;

    public Familiar() {
        setPerfil(Perfil.FAMILIAR);
    }

    public Familiar(String nome, String email, String senha, String telefone, String telegramChatId) {
        super(nome, email, senha, telefone, telegramChatId, Perfil.FAMILIAR);
    }

    public void addIdoso(Idoso idoso) {
        idosos.add(idoso);
        idoso.setFamiliar(this);
    }

    public void removeIdoso(Idoso idoso) {
        idosos.remove(idoso);
        idoso.setFamiliar(null);
    }

    public void addMedicamento(Medicamento medicamento) {
        medicamentosCadastrados.add(medicamento);
        medicamento.setFamiliar(this);
    }

    public void removeMedicamento(Medicamento medicamento) {
        medicamentosCadastrados.remove(medicamento);
        medicamento.setFamiliar(null);
    }

    /**
     * Para a entidade Familiar, apenas o serviço de medicamentos é necessário.
     */
    public Boolean isTodosBotsConectados() {
        return Boolean.TRUE.equals(botMedicamentosConectado);
    }

    public List<Idoso> getIdosos() { return idosos; }
    public void setIdosos(List<Idoso> idosos) { this.idosos = idosos; }

    public List<Medicamento> getMedicamentosCadastrados() { return medicamentosCadastrados; }
    public void setMedicamentosCadastrados(List<Medicamento> medicamentosCadastrados) { this.medicamentosCadastrados = medicamentosCadastrados; }

    public Boolean getBotMedicamentosConectado() { return botMedicamentosConectado; }
    public Boolean isBotMedicamentosConectado() { return Boolean.TRUE.equals(botMedicamentosConectado); }
    public void setBotMedicamentosConectado(Boolean botMedicamentosConectado) { this.botMedicamentosConectado = botMedicamentosConectado; }

    public Boolean getBotEventosConectado() { return botEventosConectado; }
    public Boolean isBotEventosConectado() { return Boolean.TRUE.equals(botEventosConectado); }
    public void setBotEventosConectado(Boolean botEventosConectado) { this.botEventosConectado = botEventosConectado; }

    public Boolean getBotComprasConectado() { return botComprasConectado; }
    public Boolean isBotComprasConectado() { return Boolean.TRUE.equals(botComprasConectado); }
    public void setBotComprasConectado(Boolean botComprasConectado) { this.botComprasConectado = botComprasConectado; }
}