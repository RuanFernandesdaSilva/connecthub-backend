package com.auth.simpleauth.service;

import com.auth.simpleauth.dto.DoseHistoricoDto;
import com.auth.simpleauth.entity.Dose;
import com.auth.simpleauth.enums.StatusDose;
import com.auth.simpleauth.repository.DoseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HistoricoDoseService {

    private final DoseRepository doseRepository;
    private final VinculoService vinculoService;

    public HistoricoDoseService(DoseRepository doseRepository, VinculoService vinculoService) {
        this.doseRepository = doseRepository;
        this.vinculoService = vinculoService;
    }

    public List<DoseHistoricoDto> buscarHistoricoPorFamiliarEIdoso(Long idFamiliar, Long idIdoso) {
        boolean temPermissao = vinculoService.validarPermissaoFamiliar(idFamiliar, idIdoso);
        if (!temPermissao) {
            throw new IllegalArgumentException("Familiar não possui permissão de acesso ao histórico deste idoso!");
        }

        List<Dose> dosesTomadas = doseRepository.findByMedicamentoIdosoIdAndStatusOrderByHorarioConfirmadoDesc(idIdoso, StatusDose.TOMADO);

        return dosesTomadas.stream()
                .map(DoseHistoricoDto::fromEntity)
                .collect(Collectors.toList());
    }
}