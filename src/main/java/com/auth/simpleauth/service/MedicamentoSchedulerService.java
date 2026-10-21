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

        // Incluído StatusDose.ATRASADO para manter o agendador escutando todas as doses pendentes de ação
        List<Dose> dosesParaProcessar = doseRepository.findByStatusInAndHorarioProgramadoLessThanEqual(
                List.of(StatusDose.PENDENTE, StatusDose.NOTIFICADO, StatusDose.ATRASADO),
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
                            (dose.getStatus() == StatusDose.PENDENTE || dose.getStatus() == StatusDose.NOTIFICADO || dose.getStatus() == StatusDose.ATRASADO)) {
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

        // 1ª NOTIFICAÇÃO (0 minutos)
        if (dose.getStatus() == StatusDose.PENDENTE) {
            dose.setStatus(StatusDose.NOTIFICADO);
            dose.setQuantidadeNotificacoes(1);
            dose.setProximaNotificacao(agora.plusMinutes(2)); // Próximo aviso em +2 minutos
            doseRepository.save(dose);

            String mensagemAcessivel = MensagemUtil.montarMensagemAcessivel(dose);
            medTelegramNotifier.enviarNotificacaoComBotao(telegramIdIdoso, mensagemAcessivel, dose.getId());
            return;
        }

        // 2ª NOTIFICAÇÃO (2 minutos) E ALERTA AO FAMILIAR (3 minutos)
        if ((dose.getStatus() == StatusDose.NOTIFICADO || dose.getStatus() == StatusDose.ATRASADO) && dose.getProximaNotificacao() != null) {
            if (agora.isAfter(dose.getProximaNotificacao())) {
                int tentativas = dose.getQuantidadeNotificacoes() + 1;
                dose.setQuantidadeNotificacoes(tentativas);

                if (tentativas == 2) {
                    // Reaviso ao idoso e agendamento para +1 minuto (totalizando 3 minutos)
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
                    // Limite atingido (3 min): Altera para ATRASADO e alerta o familiar
                    dose.setStatus(StatusDose.ATRASADO);
                    dose.setProximaNotificacao(null); // Finaliza reavisos automáticos ao idoso
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
        Familiar familiar = med.getIdoso() != null ? med.getIdoso().getFamiliar() : null;
        if (familiar != null && familiar.getTelegramChatId() != null && !familiar.getTelegramChatId().isBlank()) {
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
        Familiar familiar = dose.getMedicamento().getIdoso() != null ? dose.getMedicamento().getIdoso().getFamiliar() : null;
        if (familiar != null && familiar.getTelegramChatId() != null && !familiar.getTelegramChatId().isBlank()) {
            String msgFamiliar = String.format(
                    "🚨 <b>ATENÇÃO: ALERTA DE ATRASO</b>\n\n" +
                            "O idoso <b>%s</b> NÃO confirmou a dose do medicamento '<b>%s</b>' programada para as <b>%s</b>!\n\n" +
                            "Por favor, entre em contato para verificar.",
                    dose.getMedicamento().getIdoso().getNome(),
                    dose.getMedicamento().getNome(),
                    dose.getHorarioProgramado().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))
            );
            medTelegramNotifier.enviarNotificacao(familiar.getTelegramChatId(), msgFamiliar);
        } else {
            System.err.println("⚠️ Não foi possível notificar o familiar: Chat ID ausente ou não vinculado para o idoso " + dose.getMedicamento().getIdoso().getNome());
        }
    }
}