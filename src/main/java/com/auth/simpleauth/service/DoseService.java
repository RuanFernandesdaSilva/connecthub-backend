package com.auth.simpleauth.service;

import com.auth.simpleauth.config.TestableClock;
import com.auth.simpleauth.entity.Dose;
import com.auth.simpleauth.entity.Medicamento;
import com.auth.simpleauth.enums.StatusDose;
import com.auth.simpleauth.repository.DoseRepository;
import com.auth.simpleauth.repository.MedicamentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DoseService {

    private final DoseRepository doseRepository;
    private final MedicamentoRepository medicamentoRepository;
    private final TestableClock clock;
    private final DoseSchedulerEngine doseSchedulerEngine;

    public DoseService(DoseRepository doseRepository,
                       MedicamentoRepository medicamentoRepository,
                       TestableClock clock,
                       DoseSchedulerEngine doseSchedulerEngine) {
        this.doseRepository = doseRepository;
        this.medicamentoRepository = medicamentoRepository;
        this.clock = clock;
        this.doseSchedulerEngine = doseSchedulerEngine;
    }

    public List<Dose> listarPorIdoso(Long idosoId) {
        return doseRepository.findByMedicamentoIdosoId(idosoId);
    }

    public Optional<Dose> buscarPorId(Long id) {
        return doseRepository.findById(id);
    }

    @Transactional
    public Dose confirmarDose(Long doseId) {
        Dose dose = doseRepository.findById(doseId)
                .orElseThrow(() -> new RuntimeException("Dose não encontrada com ID: " + doseId));

        if (dose.getStatus() == StatusDose.TOMADO) {
            return dose;
        }

        LocalDateTime agoraSimulado = LocalDateTime.now(clock);

        int linhasAfetadas = doseRepository.atualizarStatusSePendente(
                doseId,
                StatusDose.TOMADO,
                agoraSimulado,
                StatusDose.TOMADO
        );

        if (linhasAfetadas == 0) {
            return dose;
        }

        dose.setStatus(StatusDose.TOMADO);
        dose.setHorarioConfirmado(agoraSimulado);
        dose.setProximaNotificacao(null);

        gerarProximaDose(dose.getMedicamento(), dose.getHorarioProgramado());

        return dose;
    }

    @Transactional
    public Dose marcarComoAtrasadaOuPerdida(Long doseId, StatusDose novoStatus) {
        Dose dose = doseRepository.findById(doseId)
                .orElseThrow(() -> new RuntimeException("Dose não encontrada com ID: " + doseId));

        dose.setStatus(novoStatus);
        dose.setProximaNotificacao(null);
        return doseRepository.save(dose);
    }

    private void gerarProximaDose(Medicamento medicamento, LocalDateTime horarioAtual) {
        if (medicamento == null || Boolean.FALSE.equals(medicamento.getAtivo())) {
            return;
        }

        LocalDateTime agoraSimulado = LocalDateTime.now(clock);
        LocalDateTime proximoHorario = horarioAtual.plusHours(medicamento.getIntervaloHoras());


        while (proximoHorario.isBefore(agoraSimulado)) {
            proximoHorario = proximoHorario.plusHours(medicamento.getIntervaloHoras());
        }

        if (medicamento.getDataFimTratamento() != null && proximoHorario.isAfter(medicamento.getDataFimTratamento())) {
            medicamento.setAtivo(false);
            medicamentoRepository.save(medicamento);
            return;
        }

        Dose proximaDose = new Dose(medicamento, proximoHorario);
        proximaDose = doseRepository.save(proximaDose);

        doseSchedulerEngine.agendarDose(proximaDose);
    }
}