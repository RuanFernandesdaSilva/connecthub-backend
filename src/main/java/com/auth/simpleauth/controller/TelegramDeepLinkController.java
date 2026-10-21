package com.auth.simpleauth.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/telegram/links")

public class TelegramDeepLinkController {

    @Value("${telegram.bot.med.username}")
    private String botMedUsername;

    @Value("${telegram.bot.evento.username}")
    private String botEventoUsername;

    @Value("${telegram.bot.compra.username}")
    private String botCompraUsername;

    @GetMapping("/idoso/{idosoId}")
    public ResponseEntity<Map<String, String>> obterLinksIdoso(@PathVariable Long idosoId) {
        Map<String, String> links = Map.of(
                "linkMed", String.format("https://t.me/%s?start=IDOSO_%d", botMedUsername, idosoId),
                "linkEvento", String.format("https://t.me/%s?start=IDOSO_%d", botEventoUsername, idosoId),
                "linkCompra", String.format("https://t.me/%s?start=IDOSO_%d", botCompraUsername, idosoId)
        );
        return ResponseEntity.ok(links);
    }

    @GetMapping("/familiar/{familiarId}")
    public ResponseEntity<Map<String, String>> obterLinksFamiliar(@PathVariable Long familiarId) {
        Map<String, String> links = Map.of(
                "linkMed", String.format("https://t.me/%s?start=FAMILIAR_%d", botMedUsername, familiarId),
                "linkEvento", String.format("https://t.me/%s?start=FAMILIAR_%d", botEventoUsername, familiarId),
                "linkCompra", String.format("https://t.me/%s?start=FAMILIAR_%d", botCompraUsername, familiarId)
        );
        return ResponseEntity.ok(links);
    }
}