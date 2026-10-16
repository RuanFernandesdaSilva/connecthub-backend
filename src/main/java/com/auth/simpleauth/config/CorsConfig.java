package com.auth.simpleauth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // O allowedOriginPatterns aceita tanto origens exatas quanto wildcards (*)
                .allowedOriginPatterns(
                        "https://connecthub-frontend-three.vercel.app",
                        "https://*.vercel.app",
                        "http://127.0.0.1:5500",
                        "http://localhost:5500",
                        "http://localhost:3000"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Set-Cookie", "Authorization")
                .allowCredentials(true)
                .maxAge(3600);
    }
}