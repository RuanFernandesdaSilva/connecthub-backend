package com.auth.simpleauth.controller;

import com.auth.simpleauth.dto.MedicamentoDto;
import com.auth.simpleauth.dto.MedicamentoResponseDto;
import com.auth.simpleauth.entity.Medicamento;
import com.auth.simpleauth.service.MedicamentoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/medicamentos")

public class MedicamentoController {

    private final MedicamentoService service;

    public MedicamentoController(MedicamentoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<MedicamentoResponseDto> cadastrar(@RequestBody MedicamentoDto dto) {
        Medicamento medicamento = service.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MedicamentoResponseDto(medicamento));
    }

    @GetMapping
    public ResponseEntity<List<MedicamentoResponseDto>> listar() {
        List<MedicamentoResponseDto> lista = service.listar().stream()
                .map(MedicamentoResponseDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/idoso/{idosoId}")
    public ResponseEntity<List<MedicamentoResponseDto>> listarPorIdoso(@PathVariable Long idosoId) {
        List<MedicamentoResponseDto> lista = service.listarPorIdoso(idosoId).stream()
                .map(MedicamentoResponseDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicamentoResponseDto> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(m -> ResponseEntity.ok(new MedicamentoResponseDto(m)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicamentoResponseDto> atualizar(@PathVariable Long id, @RequestBody MedicamentoDto dto) {
        Medicamento medicamento = service.atualizar(id, dto);
        return ResponseEntity.ok(new MedicamentoResponseDto(medicamento));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        service.desativar(id);
        return ResponseEntity.noContent().build();
    }
}