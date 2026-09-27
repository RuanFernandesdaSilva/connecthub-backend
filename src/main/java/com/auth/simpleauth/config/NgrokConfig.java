package com.auth.simpleauth.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class NgrokConfig implements CommandLineRunner {

    @Override
    public void run(String... args) {
        // Coloque a URL que foi gerada no seu terminal CMD via SSH
        String ngrokUrl = "https://twitter-sincere-candied.ngrok-free.dev";

        // Injeta a URL nas propriedades do sistema que a sua aplicação consome
        System.setProperty("app.frontend.url", ngrokUrl);

        System.out.println("\n--------------------------------------------------");
        System.out.println("✅ Ngrok configurado via SSH!");
        System.out.println("🌐 URL pública utilizada: " + ngrokUrl);
        System.out.println("--------------------------------------------------\n");
    }
}