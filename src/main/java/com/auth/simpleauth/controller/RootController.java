package com.auth.simpleauth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {

    @GetMapping("/")
    public String statusApi() {
        return "Servidor Vitalis / ConnectHub está online e operacional!";
    }
}
