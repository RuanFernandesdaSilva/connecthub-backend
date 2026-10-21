package com.auth.simpleauth.entity;

import com.auth.simpleauth.enums.Perfil;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("IDOSO")
public class Idoso extends Usuario {

    @ManyToOne
    @JoinColumn(name = "id_familiar", nullable = true)
    private Familiar familiar;

    @OneToMany(mappedBy = "idoso")
    private List<Medicamento> medicamentos = new ArrayList<>();

    private Boolean botMedicamentosConectado = false;
    private Boolean botEventosConectado = false;
    private Boolean botComprasConectado = false;

    public Idoso() {
        super();
        setPerfil(Perfil.IDOSO);
    }

    public Idoso(String nome, String email, String senha, String telefone, String telegramChatId, Familiar familiar) {
        super(nome, email, senha, telefone, telegramChatId, Perfil.IDOSO);
        this.familiar = familiar;
    }

    /**
     * Para a entidade Idoso, todos os 3 serviços (Medicamentos, Eventos e Compras) devem estar conectados.
     */
    public Boolean isTodosBotsConectados() {
        return Boolean.TRUE.equals(botMedicamentosConectado) &&
                Boolean.TRUE.equals(botEventosConectado) &&
                Boolean.TRUE.equals(botComprasConectado);
    }

    public Familiar getFamiliar() { return familiar; }
    public void setFamiliar(Familiar familiar) { this.familiar = familiar; }

    public List<Medicamento> getMedicamentos() { return medicamentos; }
    public void setMedicamentos(List<Medicamento> medicamentos) { this.medicamentos = medicamentos; }

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