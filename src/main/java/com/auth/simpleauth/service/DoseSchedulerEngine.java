package com.auth.simpleauth.service;

import com.auth.simpleauth.config.TestableClock;
import com.auth.simpleauth.entity.Dose;
import com.auth.simpleauth.enums.StatusDose;
import com.auth.simpleauth.repository.DoseRepository;
import com.auth.simpleauth.util.MensagemUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;

@Service
public class DoseSchedulerEngine {

    private final ThreadPoolTaskScheduler taskScheduler;
    private final DoseRepository doseRepository;
    private final MedTelegramNotifier medTelegramNotifier;
    private final TestableClock clock;

    public DoseSchedulerEngine(@Qualifier("notificationTaskScheduler") ThreadPoolTaskScheduler taskScheduler,
                               DoseRepository doseRepository,
                               MedTelegramNotifier medTelegramNotifier,
                               TestableClock clock) {
        this.taskScheduler = taskScheduler;
        this.doseRepository = doseRepository;
        this.medTelegramNotifier = medTelegramNotifier;
        this.clock = clock;
    }

    public void agendarDose(Dose dose) {
        if (dose == null || dose.getStatus() == StatusDose.TOMADO || dose.getStatus() == StatusDose.CANCELADO) {
            return;
        }

        LocalDateTime agora = LocalDateTime.now(clock);
        LocalDateTime horarioAlvo = dose.getHorarioProgramado();

        long delayMillis = Duration.between(agora, horarioAlvo).toMillis();

        if (delayMillis <= 0) {
            return;
        }

        Instant momentoExecucao = clock.instant().plusMillis(delayMillis);

        taskScheduler.schedule(() -> executarNotificacaoDose(dose.getId()), momentoExecucao);
    }

    private void executarNotificacaoDose(Long doseId) {
        doseRepository.findById(doseId).ifPresent(dose -> {
            if (dose.getStatus() == StatusDose.PENDENTE && Boolean.TRUE.equals(dose.getMedicamento().getAtivo())) {
                LocalDateTime agora = LocalDateTime.now(clock);
                dose.setStatus(StatusDose.NOTIFICADO);
                dose.setQuantidadeNotificacoes(1);
                dose.setProximaNotificacao(agora.plusMinutes(2));
                doseRepository.save(dose);

                String chatId = dose.getMedicamento().getIdoso().getTelegramChatId();
                String mensagem = MensagemUtil.montarMensagemAcessivel(dose);
                medTelegramNotifier.enviarNotificacaoComBotao(chatId, mensagem, dose.getId());
            }
        });
    }
}