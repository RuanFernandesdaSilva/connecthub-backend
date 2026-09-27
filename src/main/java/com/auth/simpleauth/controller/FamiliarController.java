package com.auth.simpleauth.controller;

import com.auth.simpleauth.dto.AtualizarFotoDto;
import com.auth.simpleauth.dto.FamiliarDto;
import com.auth.simpleauth.dto.FamiliarResponseDto;
import com.auth.simpleauth.entity.Familiar;
import com.auth.simpleauth.service.FamiliarService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/familiares")

public class FamiliarController {

    private final FamiliarService service;

    public FamiliarController(FamiliarService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<FamiliarResponseDto> cadastrar(@RequestBody FamiliarDto dto) {
        Familiar novoFamiliar = service.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new FamiliarResponseDto(novoFamiliar));
    }

    @GetMapping
    public ResponseEntity<List<FamiliarResponseDto>> listar() {
        List<FamiliarResponseDto> lista = service.listar().stream()
                .map(FamiliarResponseDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FamiliarResponseDto> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(f -> ResponseEntity.ok(new FamiliarResponseDto(f)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<FamiliarResponseDto> atualizar(@PathVariable Long id, @RequestBody FamiliarDto dto) {
        Familiar familiarAtualizado = service.atualizar(id, dto);
        return ResponseEntity.ok(new FamiliarResponseDto(familiarAtualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        service.remover(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/foto")
    public ResponseEntity<Void> atualizarFotoPerfil(@PathVariable Long id, @RequestBody AtualizarFotoDto dto) {
        service.atualizarFotoPerfil(id, dto.getImagemUrl());
        return ResponseEntity.ok().build();
    }
}