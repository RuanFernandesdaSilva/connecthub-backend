package com.auth.simpleauth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // Registre explicitamente as origens exatas do frontend
                .allowedOrigins(
                        "https://connecthub-frontend-three.vercel.app",
                        "http://127.0.0.1:5500",
                        "http://localhost:5500",
                        "http://localhost:3000"
                )
                .allowedOriginPatterns(
                        "https://*.vercel.app",
                        "https://*.ngrok-free.app"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Set-Cookie", "Authorization")
                .allowCredentials(true)
                .maxAge(3600); // Salva o preflight CORS por 1 hora no navegador
    }
}