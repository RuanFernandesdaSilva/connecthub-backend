package com.auth.simpleauth.controller;

import com.auth.simpleauth.service.QrCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/qrcode")

public class QrCodeController {

    @Autowired
    private QrCodeService qrCodeService;

    @GetMapping(value = "/{id}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> obterQrCodeDoIdoso(@PathVariable("id") Long idosoId) {
        try {
            byte[] imagemQrCode = qrCodeService.gerarQrCodePorId(idosoId, 250, 250);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);

            return new ResponseEntity<>(imagemQrCode, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace(); // Loga o erro no console do Spring Boot para podermos identificar se o ZXing falhar
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}