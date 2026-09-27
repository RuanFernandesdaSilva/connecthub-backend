package com.auth.simpleauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SimpleAuthApplication {

    public static void main(String[] args) {
        // Desativa a tentativa de uso de SOCKS/Proxy do sistema pela JVM
        System.setProperty("java.net.useSystemProxies", "false");

        SpringApplication.run(SimpleAuthApplication.class, args);
    }

}