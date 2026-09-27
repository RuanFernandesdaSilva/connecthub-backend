package com.auth.simpleauth.controller;

import com.auth.simpleauth.dto.DoseResponseDto;
import com.auth.simpleauth.entity.Dose;
import com.auth.simpleauth.service.DoseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/doses")
public class DoseController {

    private final DoseService service;

    public DoseController(DoseService service) {
        this.service = service;
    }

    @GetMapping("/idoso/{idosoId}")
    public ResponseEntity<List<DoseResponseDto>> listarPorIdoso(@PathVariable Long idosoId) {
        List<DoseResponseDto> lista = service.listarPorIdoso(idosoId).stream()
                .map(DoseResponseDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @PutMapping("/{id}/confirmar")
    public ResponseEntity<DoseResponseDto> confirmarDose(@PathVariable Long id) {
        Dose dose = service.confirmarDose(id);
        return ResponseEntity.ok(new DoseResponseDto(dose));
    }
}