package com.auth.simpleauth.service;

import com.auth.simpleauth.repository.FamiliarRepository;
import com.auth.simpleauth.repository.IdosoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class WebAppActivationService {

    @Value("${telegram.bot.med.token:}")
    private String tokenBotMed;

    @Value("${telegram.bot.evento.token:}")
    private String tokenBotEvento;

    @Value("${telegram.bot.compra.token:}")
    private String tokenBotCompra;

    private final IdosoRepository idosoRepository;
    private final FamiliarRepository familiarRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    public WebAppActivationService(IdosoRepository idosoRepository, FamiliarRepository familiarRepository) {
        this.idosoRepository = idosoRepository;
        this.familiarRepository = familiarRepository;
    }

    public boolean ativarBotSecundario(String token, String servico) {
        if (token == null || !token.contains("_")) return false;

        String[] partes = token.split("_");
        String tipo = partes[0];
        Long id = Long.parseLong(partes[1]);
        String chatId = null;
        String nome = "";

        if ("IDOSO".equalsIgnoreCase(tipo)) {
            var idosoOpt = idosoRepository.findById(id);
            if (idosoOpt.isPresent()) {
                chatId = idosoOpt.get().getTelegramChatId();
                nome = idosoOpt.get().getNome();
            }
        } else if ("FAMILIAR".equalsIgnoreCase(tipo)) {
            var familiarOpt = familiarRepository.findById(id);
            if (familiarOpt.isPresent()) {
                chatId = familiarOpt.get().getTelegramChatId();
                nome = familiarOpt.get().getNome();
            }
        }

        if (chatId == null) return false;

        // Seleciona o Token do Bot Secundário correto
        String botTokenTarget = switch (servico.toUpperCase()) {
            case "MEDICAMENTOS" -> tokenBotMed;
            case "EVENTOS" -> tokenBotEvento;
            case "COMPRAS" -> tokenBotCompra;
            default -> null;
        };

        if (botTokenTarget == null || botTokenTarget.isBlank()) return false;

        // Envia mensagem em segundo plano via API Bot do Telegram (Equivalente ao /start do bot secundário)
        String urlApiTelegram = String.format("https://api.telegram.org/bot%s/sendMessage", botTokenTarget);
        String mensagem = String.format("✅ Olá <b>%s</b>! O serviço de <b>%s</b> foi ativado com sucesso para a sua conta.", nome, servico);

        Map<String, Object> body = Map.of(
                "chat_id", chatId,
                "text", mensagem,
                "parse_mode", "HTML"
        );

        try {
            restTemplate.postForEntity(urlApiTelegram, body, String.class);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}