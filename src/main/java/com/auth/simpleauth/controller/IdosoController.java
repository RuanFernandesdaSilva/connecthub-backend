package com.auth.simpleauth.controller;

import com.auth.simpleauth.dto.IdosoDto;
import com.auth.simpleauth.dto.IdosoResponseDto;
import com.auth.simpleauth.entity.Idoso;
import com.auth.simpleauth.service.IdosoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/idosos")

public class IdosoController {

    private final IdosoService service;

    public IdosoController(IdosoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<IdosoResponseDto> cadastrar(@RequestBody IdosoDto dto) {
        Idoso novoIdoso = service.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new IdosoResponseDto(novoIdoso));
    }

    @GetMapping
    public ResponseEntity<List<IdosoResponseDto>> listar() {
        List<IdosoResponseDto> lista = service.listar().stream()
                .map(IdosoResponseDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IdosoResponseDto> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(i -> ResponseEntity.ok(new IdosoResponseDto(i)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<IdosoResponseDto> atualizar(@PathVariable Long id, @RequestBody IdosoDto dto) {
        Idoso idosoAtualizado = service.atualizar(id, dto);
        return ResponseEntity.ok(new IdosoResponseDto(idosoAtualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        service.remover(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/vincular-familiar-emergencial")
    public ResponseEntity<String> vincularFamiliarEmergencial() {
        try {
            // Busca todos os familiares cadastrados no banco
            List<com.auth.simpleauth.entity.Familiar> familiares = service.listarFamiliares();
            if (familiares.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("❌ Nenhum familiar encontrado no banco de dados.");
            }

            // Pega o primeiro familiar (ex: Eduardo)
            com.auth.simpleauth.entity.Familiar familiarPadrao = familiares.get(0);

            // Busca todos os idosos sem familiar associado
            List<Idoso> idososSemFamiliar = service.listar().stream()
                    .filter(i -> i.getFamiliar() == null)
                    .toList();

            if (idososSemFamiliar.isEmpty()) {
                return ResponseEntity.ok("✅ Todos os idosos já possuem um familiar associado!");
            }

            // Atualiza cada idoso atribuindo o familiar
            for (Idoso idoso : idososSemFamiliar) {
                idoso.setFamiliar(familiarPadrao);
                com.auth.simpleauth.dto.IdosoDto dto = new com.auth.simpleauth.dto.IdosoDto();
                dto.setIdFamiliar(familiarPadrao.getId());
                service.atualizar(idoso.getId(), dto);
            }

            return ResponseEntity.ok(String.format(
                    "🎉 Sucesso! %d idoso(s) foram vinculados ao familiar '%s' (ID: %d).",
                    idososSemFamiliar.size(),
                    familiarPadrao.getNome(),
                    familiarPadrao.getId()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ Erro ao vincular: " + e.getMessage());
        }
    }
}