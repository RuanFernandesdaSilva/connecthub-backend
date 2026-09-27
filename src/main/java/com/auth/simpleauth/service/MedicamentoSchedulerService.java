package com.auth.simpleauth.service;

import com.auth.simpleauth.config.TestableClock;
import com.auth.simpleauth.entity.Dose;
import com.auth.simpleauth.entity.Familiar;
import com.auth.simpleauth.entity.Medicamento;
import com.auth.simpleauth.enums.StatusDose;
import com.auth.simpleauth.repository.DoseRepository;
import com.auth.simpleauth.repository.MedicamentoRepository;
import com.auth.simpleauth.util.MensagemUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class MedicamentoSchedulerService {

    private final DoseRepository doseRepository;
    private final MedicamentoRepository medicamentoRepository;
    private final MedTelegramNotifier medTelegramNotifier;
    private final TestableClock clock;

    public MedicamentoSchedulerService(DoseRepository doseRepository,
                                       MedicamentoRepository medicamentoRepository,
                                       MedTelegramNotifier medTelegramNotifier,
                                       TestableClock clock) {
        this.doseRepository = doseRepository;
        this.medicamentoRepository = medicamentoRepository;
        this.medTelegramNotifier = medTelegramNotifier;
        this.clock = clock;
    }

    @Scheduled(fixedRate = 3000)
    @Transactional
    public void verificarDosesAgendadas() {
        LocalDateTime agora = LocalDateTime.now(clock);

        List<Dose> dosesParaProcessar = doseRepository.findByStatusInAndHorarioProgramadoLessThanEqual(
                List.of(StatusDose.PENDENTE, StatusDose.NOTIFICADO),
                agora
        );

        for (Dose dose : dosesParaProcessar) {
            processarDose(dose, agora);
        }
    }

    @Scheduled(fixedRate = 10000)
    public void verificarTratamentosExpirados() {
        LocalDateTime agora = LocalDateTime.now(clock);

        List<Medicamento> medicamentosExpirados = medicamentoRepository
                .findByAtivoTrueAndDataFimTratamentoLessThanEqual(agora);

        for (Medicamento med : medicamentosExpirados) {
            try {
                med.setAtivo(false);
                medicamentoRepository.save(med);

                List<Dose> dosesPendentes = doseRepository.findByMedicamentoIdosoId(med.getIdoso().getId());
                for (Dose dose : dosesPendentes) {
                    if (dose.getMedicamento().getId().equals(med.getId()) &&
                            (dose.getStatus() == StatusDose.PENDENTE || dose.getStatus() == StatusDose.NOTIFICADO)) {
                        dose.setStatus(StatusDose.CANCELADO);
                        dose.setProximaNotificacao(null);
                        doseRepository.save(dose);
                    }
                }

                notificarFamiliarTerminoTratamento(med);

            } catch (Exception e) {
                System.err.println("Erro ao processar inativação do medicamento ID " + med.getId() + ": " + e.getMessage());
            }
        }
    }

    private void processarDose(Dose dose, LocalDateTime agora) {
        Medicamento med = dose.getMedicamento();

        if (Boolean.FALSE.equals(med.getAtivo())) {
            dose.setStatus(StatusDose.CANCELADO);
            doseRepository.save(dose);
            return;
        }

        if (med.getDataFimTratamento() != null && agora.isAfter(med.getDataFimTratamento())) {
            inativarTratamentoExpirado(med, dose);
            return;
        }

        String nomeMedicamento = med.getNome();
        String nomeIdoso = med.getIdoso().getNome();
        String telegramIdIdoso = med.getIdoso().getTelegramChatId();

        if (dose.getStatus() == StatusDose.PENDENTE) {
            dose.setStatus(StatusDose.NOTIFICADO);
            dose.setQuantidadeNotificacoes(1);
            dose.setProximaNotificacao(agora.plusMinutes(2));
            doseRepository.save(dose);

            String mensagemAcessivel = MensagemUtil.montarMensagemAcessivel(dose);
            medTelegramNotifier.enviarNotificacaoComBotao(telegramIdIdoso, mensagemAcessivel, dose.getId());
            return;
        }

        if (dose.getStatus() == StatusDose.NOTIFICADO && dose.getProximaNotificacao() != null) {
            if (agora.isAfter(dose.getProximaNotificacao())) {
                int tentativas = dose.getQuantidadeNotificacoes() + 1;
                dose.setQuantidadeNotificacoes(tentativas);

                if (tentativas == 2) {
                    dose.setProximaNotificacao(agora.plusMinutes(1));
                    doseRepository.save(dose);

                    String emojiCor = MensagemUtil.obterEmojiCor(med.getCorCaixa());
                    String msgReiteracao = String.format(
                            "⚠️ %s, você ainda não confirmou o remédio da caixa %s <b>%s</b> ('<b>%s</b>')!\n" +
                                    "Por favor, clique no botão abaixo para confirmar.",
                            nomeIdoso,
                            emojiCor,
                            med.getCorCaixa() != null ? med.getCorCaixa().toUpperCase() : "",
                            nomeMedicamento
                    );
                    medTelegramNotifier.enviarNotificacaoComBotao(telegramIdIdoso, msgReiteracao, dose.getId());

                } else {
                    dose.setStatus(StatusDose.ATRASADO);
                    doseRepository.save(dose);

                    notificarFamiliarAtraso(dose);
                }
            }
        }
    }

    private void inativarTratamentoExpirado(Medicamento med, Dose dose) {
        med.setAtivo(false);
        medicamentoRepository.save(med);

        dose.setStatus(StatusDose.CANCELADO);
        doseRepository.save(dose);

        notificarFamiliarTerminoTratamento(med);
    }

    private void notificarFamiliarTerminoTratamento(Medicamento med) {
        Familiar familiar = med.getIdoso().getFamiliar();
        if (familiar != null && familiar.getTelegramChatId() != null) {
            String msgFamiliar = String.format(
                    "📋 <b>TRATAMENTO CONCLUÍDO</b>\n\n" +
                            "O tratamento do medicamento <b>%s</b> para o idoso <b>%s</b> atingiu a data de término prevista (%s).\n\n" +
                            "ℹ️ O medicamento foi inativado automaticamente e não gerará novos lembretes.",
                    med.getNome(),
                    med.getIdoso().getNome(),
                    med.getDataFimTratamento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
            );
            medTelegramNotifier.enviarNotificacao(familiar.getTelegramChatId(), msgFamiliar);
        }
    }

    private void notificarFamiliarAtraso(Dose dose) {
        Familiar familiar = dose.getMedicamento().getIdoso().getFamiliar();
        if (familiar != null && familiar.getTelegramChatId() != null) {
            String msgFamiliar = String.format(
                    "🚨 ATENÇÃO: O idoso %s NÃO confirmou a dose do medicamento '%s' programada para as %s!",
                    dose.getMedicamento().getIdoso().getNome(),
                    dose.getMedicamento().getNome(),
                    dose.getHorarioProgramado().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))
            );
            medTelegramNotifier.enviarNotificacao(familiar.getTelegramChatId(), msgFamiliar);
        }
    }
}