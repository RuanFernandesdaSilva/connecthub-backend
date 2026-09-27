package com.auth.simpleauth.service;

import com.auth.simpleauth.dto.MedicamentoDto;
import com.auth.simpleauth.entity.Dose;
import com.auth.simpleauth.entity.Idoso;
import com.auth.simpleauth.entity.Medicamento;
import com.auth.simpleauth.enums.StatusDose;
import com.auth.simpleauth.enums.TipoTratamento;
import com.auth.simpleauth.enums.UnidadeTempoDuracao;
import com.auth.simpleauth.repository.DoseRepository;
import com.auth.simpleauth.repository.IdosoRepository;
import com.auth.simpleauth.repository.MedicamentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MedicamentoService {

    private final MedicamentoRepository medicamentoRepository;
    private final IdosoRepository idosoRepository;
    private final DoseRepository doseRepository;
    private final DoseSchedulerEngine doseSchedulerEngine;

    public MedicamentoService(MedicamentoRepository medicamentoRepository,
                              IdosoRepository idosoRepository,
                              DoseRepository doseRepository,
                              DoseSchedulerEngine doseSchedulerEngine) {
        this.medicamentoRepository = medicamentoRepository;
        this.idosoRepository = idosoRepository;
        this.doseRepository = doseRepository;
        this.doseSchedulerEngine = doseSchedulerEngine;
    }

    @Transactional
    public Medicamento cadastrar(MedicamentoDto dto) {
        Idoso idoso = idosoRepository.findById(dto.getIdIdoso())
                .orElseThrow(() -> new IllegalArgumentException("Idoso não encontrado com ID: " + dto.getIdIdoso()));

        UnidadeTempoDuracao unidadeDuracao = resolverUnidadeDuracao(dto);
        Integer valorDuracao = (dto.getTipoTratamento() == TipoTratamento.CONTINUO) ? null : dto.getValorDuracao();
        LocalDateTime dataFim = calcularDataFimTratamento(dto.getPrimeiroHorario(), dto.getTipoTratamento(), unidadeDuracao, valorDuracao);

        Medicamento medicamento = new Medicamento(
                dto.getNome(),
                dto.getDosagem(),
                dto.getUnidade(),
                dto.getQuantidadePorDose(),
                dto.getPrimeiroHorario(),
                dto.getIntervaloHoras(),
                dto.getCorCaixa(),
                dto.getIdentificadorCaixa(),
                dto.getTipoTratamento(),
                unidadeDuracao,
                valorDuracao,
                dataFim,
                idoso,
                idoso.getFamiliar()
        );

        Medicamento medicamentoSalvo = medicamentoRepository.save(medicamento);
        gerarPrimeiraDose(medicamentoSalvo);

        return medicamentoSalvo;
    }

    private void gerarPrimeiraDose(Medicamento medicamento) {
        Dose primeiraDose = new Dose(medicamento, medicamento.getPrimeiroHorario());
        Dose doseSalva = doseRepository.save(primeiraDose);
        doseSchedulerEngine.agendarDose(doseSalva);
    }

    public List<Medicamento> listar() {
        return medicamentoRepository.findAll();
    }

    public List<Medicamento> listarPorIdoso(Long idosoId) {
        return medicamentoRepository.findByIdosoIdAndAtivoTrue(idosoId);
    }

    public Optional<Medicamento> buscarPorId(Long id) {
        return medicamentoRepository.findById(id);
    }

    @Transactional
    public Medicamento atualizar(Long id, MedicamentoDto dto) {
        Medicamento medicamento = medicamentoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Medicamento não encontrado com ID: " + id));

        boolean horarioOuIntervaloMudou = !medicamento.getPrimeiroHorario().equals(dto.getPrimeiroHorario()) ||
                !medicamento.getIntervaloHoras().equals(dto.getIntervaloHoras());

        medicamento.setNome(dto.getNome());
        medicamento.setDosagem(dto.getDosagem());
        medicamento.setUnidade(dto.getUnidade());
        medicamento.setQuantidadePorDose(dto.getQuantidadePorDose());
        medicamento.setPrimeiroHorario(dto.getPrimeiroHorario());
        medicamento.setIntervaloHoras(dto.getIntervaloHoras());
        medicamento.setCorCaixa(dto.getCorCaixa());
        medicamento.setIdentificadorCaixa(dto.getIdentificadorCaixa());

        UnidadeTempoDuracao unidadeDuracao = resolverUnidadeDuracao(dto);
        Integer valorDuracao = (dto.getTipoTratamento() == TipoTratamento.CONTINUO) ? null : dto.getValorDuracao();
        LocalDateTime dataFim = calcularDataFimTratamento(dto.getPrimeiroHorario(), dto.getTipoTratamento(), unidadeDuracao, valorDuracao);

        medicamento.setTipoTratamento(dto.getTipoTratamento());
        medicamento.setUnidadeDuracao(unidadeDuracao);
        medicamento.setValorDuracao(valorDuracao);
        medicamento.setDataFimTratamento(dataFim);

        if (dto.getIdIdoso() != null) {
            Idoso idoso = idosoRepository.findById(dto.getIdIdoso())
                    .orElseThrow(() -> new IllegalArgumentException("Idoso não encontrado com ID: " + dto.getIdIdoso()));
            medicamento.setIdoso(idoso);
            medicamento.setFamiliar(idoso.getFamiliar());
        }

        Medicamento medicamentoAtualizado = medicamentoRepository.save(medicamento);

        // Se mudou o cronograma, cancela doses pendentes e re-agenda a partir do novo primeiro horário
        if (horarioOuIntervaloMudou) {
            cancelarDosesPendentesDoMedicamento(id);
            gerarPrimeiraDose(medicamentoAtualizado);
        }

        return medicamentoAtualizado;
    }

    @Transactional
    public void desativar(Long id) {
        Medicamento medicamento = medicamentoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Medicamento não encontrado com ID: " + id));

        medicamento.setAtivo(false);
        medicamentoRepository.save(medicamento);

        cancelarDosesPendentesDoMedicamento(id);
    }

    private void cancelarDosesPendentesDoMedicamento(Long medicamentoId) {
        List<Dose> doses = doseRepository.findByMedicamentoId(medicamentoId);
        for (Dose dose : doses) {
            if (dose.getStatus() == StatusDose.PENDENTE || dose.getStatus() == StatusDose.NOTIFICADO) {
                dose.setStatus(StatusDose.CANCELADO);
                dose.setProximaNotificacao(null);
                doseRepository.save(dose);
            }
        }
    }

    private UnidadeTempoDuracao resolverUnidadeDuracao(MedicamentoDto dto) {
        if (dto.getTipoTratamento() == TipoTratamento.CONTINUO) {
            return UnidadeTempoDuracao.ILIMITADO;
        }
        return dto.getUnidadeDuracao();
    }

    private LocalDateTime calcularDataFimTratamento(LocalDateTime inicio, TipoTratamento tipo, UnidadeTempoDuracao unidade, Integer valor) {
        if (tipo == TipoTratamento.CONTINUO || unidade == UnidadeTempoDuracao.ILIMITADO || valor == null || inicio == null) {
            return null;
        }

        return switch (unidade) {
            case DIAS -> inicio.plusDays(valor);
            case MESES -> inicio.plusMonths(valor);
            case ANOS -> inicio.plusYears(valor);
            default -> null;
        };
    }
}