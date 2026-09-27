package com.auth.simpleauth.service;

import com.auth.simpleauth.dto.PedidoVinculoDto;
import com.auth.simpleauth.dto.UsuarioVinculadoDto;
import com.auth.simpleauth.dto.VinculoDetalhadoResponseDto;
import com.auth.simpleauth.entity.Familiar;
import com.auth.simpleauth.entity.Idoso;
import com.auth.simpleauth.entity.Usuario;
import com.auth.simpleauth.entity.VinculoFamiliarIdoso;
import com.auth.simpleauth.enums.StatusVinculo;
import com.auth.simpleauth.repository.FamiliarRepository;
import com.auth.simpleauth.repository.IdosoRepository;
import com.auth.simpleauth.repository.UsuarioRepository;
import com.auth.simpleauth.repository.VinculoFamiliarRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VinculoService {

    private final VinculoFamiliarRepository vinculoFamiliarRepository;
    private final FamiliarRepository familiarRepository;
    private final IdosoRepository idosoRepository;
    private final UsuarioRepository usuarioRepository;

    public VinculoService(VinculoFamiliarRepository vinculoFamiliarRepository,
                          FamiliarRepository familiarRepository,
                          IdosoRepository idosoRepository,
                          UsuarioRepository usuarioRepository) {
        this.vinculoFamiliarRepository = vinculoFamiliarRepository;
        this.familiarRepository = familiarRepository;
        this.idosoRepository = idosoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public boolean validarPermissaoFamiliar(Long idFamiliar, Long idIdoso) {
        return vinculoFamiliarRepository.existsByFamiliarIdAndIdosoIdAndStatus(idFamiliar, idIdoso, StatusVinculo.ACEITO);
    }

    @Transactional
    public VinculoFamiliarIdoso solicitarVinculo(Long idFamiliar, Long idIdoso) {
        Familiar familiar = familiarRepository.findById(idFamiliar)
                .orElseThrow(() -> new IllegalArgumentException("Familiar não encontrado com ID: " + idFamiliar));

        Idoso idoso = idosoRepository.findById(idIdoso)
                .orElseThrow(() -> new IllegalArgumentException("Idoso não encontrado com ID: " + idIdoso));

        return vinculoFamiliarRepository.findByFamiliarIdAndIdosoId(idFamiliar, idIdoso)
                .map(vinculo -> {
                    if (vinculo.getStatus() == StatusVinculo.ACEITO) {
                        throw new IllegalStateException("Este vínculo já está aprovado.");
                    }
                    vinculo.setStatus(StatusVinculo.PENDENTE);
                    return vinculoFamiliarRepository.save(vinculo);
                })
                .orElseGet(() -> vinculoFamiliarRepository.save(new VinculoFamiliarIdoso(familiar, idoso, StatusVinculo.PENDENTE)));
    }

    @Transactional
    public VinculoFamiliarIdoso vincularDiretoPorQrCode(Long idFamiliar, Long idIdoso) {
        Familiar familiar = familiarRepository.findById(idFamiliar)
                .orElseThrow(() -> new IllegalArgumentException("Familiar não encontrado com ID: " + idFamiliar));

        Idoso idoso = idosoRepository.findById(idIdoso)
                .orElseThrow(() -> new IllegalArgumentException("Idoso não encontrado com ID: " + idIdoso));

        return vinculoFamiliarRepository.findByFamiliarIdAndIdosoId(idFamiliar, idIdoso)
                .map(vinculo -> {
                    vinculo.setStatus(StatusVinculo.ACEITO);
                    return vinculoFamiliarRepository.save(vinculo);
                })
                .orElseGet(() -> vinculoFamiliarRepository.save(new VinculoFamiliarIdoso(familiar, idoso, StatusVinculo.ACEITO)));
    }

    @Transactional(readOnly = true)
    public List<PedidoVinculoDto> listarPedidosPendentesDoIdoso(Long idIdoso) {
        List<VinculoFamiliarIdoso> pendentes = vinculoFamiliarRepository
                .findByIdosoIdAndStatusWithFamiliar(idIdoso, StatusVinculo.PENDENTE);

        return pendentes.stream().map(v -> new PedidoVinculoDto(
                v.getId(),
                v.getFamiliar().getId(),
                v.getFamiliar().getNome(),
                v.getFamiliar().getEmail(),
                v.getFamiliar().getImagemUrl(),
                v.getDataCriacao()
        )).collect(Collectors.toList());
    }

    @Transactional
    public String responderSolicitacao(Long vinculoId, boolean aceito) {
        VinculoFamiliarIdoso vinculo = vinculoFamiliarRepository.findById(vinculoId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitação de vínculo não encontrada. ID: " + vinculoId));

        StatusVinculo novoStatus = aceito ? StatusVinculo.ACEITO : StatusVinculo.RECUSADO;
        vinculo.setStatus(novoStatus);
        vinculoFamiliarRepository.save(vinculo);

        return aceito ? "Solicitação aceita com sucesso!" : "Solicitação recusada.";
    }

    @Transactional(readOnly = true)
    public List<UsuarioVinculadoDto> listarIdososAceitosDoFamiliar(Long idFamiliar) {
        List<VinculoFamiliarIdoso> vinculos = vinculoFamiliarRepository
                .findByFamiliarIdAndStatusWithIdoso(idFamiliar, StatusVinculo.ACEITO);

        return vinculos.stream().map(v -> new UsuarioVinculadoDto(
                v.getIdoso().getId(),
                v.getIdoso().getNome(),
                v.getIdoso().getEmail(),
                v.getIdoso().getImagemUrl()
        )).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UsuarioVinculadoDto> listarFamiliaresAceitosDoIdoso(Long idIdoso) {
        List<VinculoFamiliarIdoso> vinculos = vinculoFamiliarRepository
                .findByIdosoIdAndStatusWithFamiliar(idIdoso, StatusVinculo.ACEITO);

        return vinculos.stream().map(v -> new UsuarioVinculadoDto(
                v.getFamiliar().getId(),
                v.getFamiliar().getNome(),
                v.getFamiliar().getEmail(),
                v.getFamiliar().getImagemUrl()
        )).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public VinculoDetalhadoResponseDto obterDetalhesVinculo(Long idFamiliar, Long idIdoso) {
        Familiar familiar = familiarRepository.findById(idFamiliar)
                .orElseThrow(() -> new IllegalArgumentException("Familiar não encontrado"));

        Idoso idoso = idosoRepository.findById(idIdoso)
                .orElseThrow(() -> new IllegalArgumentException("Idoso não encontrado"));

        long totalIdosos = vinculoFamiliarRepository.countByFamiliarIdAndStatus(idFamiliar, StatusVinculo.ACEITO);
        long totalFamiliares = vinculoFamiliarRepository.countByIdosoIdAndStatus(idIdoso, StatusVinculo.ACEITO);

        return new VinculoDetalhadoResponseDto(
                familiar.getNome(),
                idoso.getNome(),
                totalIdosos,
                totalFamiliares
        );
    }

    @Transactional
    public void vincularTelegram(Long usuarioId, String telegramChatId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        usuario.setTelegramChatId(telegramChatId);
        usuarioRepository.save(usuario);
    }
}