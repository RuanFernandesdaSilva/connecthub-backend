package com.auth.simpleauth.telegram;

import com.auth.simpleauth.entity.Familiar;
import com.auth.simpleauth.entity.Idoso;
import com.auth.simpleauth.enums.StatusDose;
import com.auth.simpleauth.repository.DoseRepository;
import com.auth.simpleauth.repository.FamiliarRepository;
import com.auth.simpleauth.repository.IdosoRepository;
import com.auth.simpleauth.service.DoseService;
import com.auth.simpleauth.service.TelegramNotificationGateway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
public class ConnectHubBot extends TelegramLongPollingBot {

    @Value("${telegram.bot.hub.token:}")
    private String botToken;

    @Value("${telegram.bot.hub.username:ConnectHubCentralBot}")
    private String botUsername;

    @Value("${app.frontend.url:https://connecthub-frontend-three.vercel.app}")
    private String frontendUrl;

    private final DoseRepository doseRepository;
    private final IdosoRepository idosoRepository;
    private final FamiliarRepository familiarRepository;
    private final TelegramNotificationGateway notificationGateway;
    private final DoseService doseService;

    public ConnectHubBot(@Value("${telegram.bot.hub.token:}") String botToken,
                         DoseRepository doseRepository,
                         IdosoRepository idosoRepository,
                         FamiliarRepository familiarRepository,
                         TelegramNotificationGateway notificationGateway,
                         DoseService doseService) {
        super(criarOpcoesConexao(), botToken);
        this.doseRepository = doseRepository;
        this.idosoRepository = idosoRepository;
        this.familiarRepository = familiarRepository;
        this.notificationGateway = notificationGateway;
        this.doseService = doseService;
    }

    private static DefaultBotOptions criarOpcoesConexao() {
        DefaultBotOptions options = new DefaultBotOptions();
        options.setGetUpdatesTimeout(60);
        return options;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            processarCallback(update.getCallbackQuery());
        } else if (update.hasMessage() && update.getMessage().hasText()) {
            String texto = update.getMessage().getText();
            String chatId = String.valueOf(update.getMessage().getChatId());
            processarComandoStart(texto, chatId);
        }
    }

    private void processarComandoStart(String texto, String chatId) {
        if (texto.startsWith("/start")) {
            String[] partes = texto.split(" ");
            if (partes.length > 1) {
                String tokenParam = partes[1];
                try {
                    if (tokenParam.startsWith("IDOSO_")) {
                        processarConexaoIdoso(tokenParam, chatId);
                    } else if (tokenParam.startsWith("FAMILIAR_")) {
                        processarConexaoFamiliar(tokenParam, chatId);
                    }
                } catch (Exception e) {
                    enviarMensagemSimples(chatId, "⚠️ Ops! Algo deu errado.\nPor favor, tente clicar no link do site novamente.");
                }
            } else {
                enviarMensagemSimples(chatId, "👋 Olá!\n\nPara começar, acesse o nosso aplicativo no seu celular ou computador e toque no botão de conectar com o Telegram.");
            }
        }
    }

    private void processarConexaoIdoso(String tokenParam, String chatId) {
        String rawToken = tokenParam.replace("IDOSO_", "");
        String[] tokenParts = rawToken.split("_");
        Long idosoId = Long.parseLong(tokenParts[0]);

        idosoRepository.findById(idosoId).ifPresentOrElse(idoso -> {
            idoso.setTelegramChatId(chatId);

            if (tokenParts.length > 1) {
                atualizarStatusBotIdoso(idoso, tokenParts[1]);
            }

            idosoRepository.save(idoso);

            String baseToken = "IDOSO_" + idoso.getId();

            if (idoso.isTodosBotsConectados()) {
                enviarMensagemParabensConcluido(chatId, idoso.getNome(), idoso.getId(), "IDOSO");
            } else {
                enviarBotoesCentralIdoso(chatId, idoso.getNome(), baseToken,
                        idoso.isBotMedicamentosConectado(),
                        idoso.isBotEventosConectado(),
                        idoso.isBotComprasConectado());
            }
        }, () -> enviarMensagemSimples(chatId, "⚠️ Não encontramos o seu cadastro. Peça ajuda a um familiar."));
    }

    private void processarConexaoFamiliar(String tokenParam, String chatId) {
        String rawToken = tokenParam.replace("FAMILIAR_", "");
        String[] tokenParts = rawToken.split("_");
        Long familiarId = Long.parseLong(tokenParts[0]);

        familiarRepository.findById(familiarId).ifPresentOrElse(familiar -> {
            familiar.setTelegramChatId(chatId);

            if (tokenParts.length > 1) {
                atualizarStatusBotFamiliar(familiar, tokenParts[1]);
            }

            familiarRepository.save(familiar);

            String baseToken = "FAMILIAR_" + familiar.getId();

            if (familiar.isBotMedicamentosConectado()) {
                enviarMensagemParabensConcluido(chatId, familiar.getNome(), familiar.getId(), "FAMILIAR");
            } else {
                enviarBotaoFamiliar(chatId, familiar.getNome(), baseToken);
            }
        }, () -> enviarMensagemSimples(chatId, "⚠️ Familiar não encontrado. Verifique seu cadastro no aplicativo."));
    }

    private void atualizarStatusBotIdoso(Idoso idoso, String botTag) {
        switch (botTag) {
            case "MED" -> idoso.setBotMedicamentosConectado(true);
            case "EVT" -> idoso.setBotEventosConectado(true);
            case "CMP" -> idoso.setBotComprasConectado(true);
        }
    }

    private void atualizarStatusBotFamiliar(Familiar familiar, String botTag) {
        if ("MED".equals(botTag)) {
            familiar.setBotMedicamentosConectado(true);
        }
    }

    // 💡 Mensagem EXTREMAMENTE simples e visual para o IDOSO
    private void enviarBotoesCentralIdoso(String chatId, String nomeUsuario, String token,
                                          boolean medConectado, boolean evtConectado, boolean cmpConectado) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setParseMode("HTML");

        StringBuilder textoMsg = new StringBuilder();
        textoMsg.append(String.format("👋 Olá, <b>%s</b>!\n\n", nomeUsuario));
        textoMsg.append("Falta pouco para ativar tudo!\n");
        textoMsg.append("Toque no botão abaixo para continuar:\n\n");
        textoMsg.append("👇 <b>TOQUE ABAIXO</b> 👇");

        message.setText(textoMsg.toString());

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        if (!medConectado) {
            List<InlineKeyboardButton> row1 = new ArrayList<>();
            InlineKeyboardButton btnMed = new InlineKeyboardButton();
            btnMed.setText("👉 💊 Ativar Avisos de Remédios");
            btnMed.setWebApp(new WebAppInfo(String.format("%s/webapp/medicamento-bot.html?token=%s", frontendUrl, token)));
            row1.add(btnMed);
            rows.add(row1);
        }

        if (!evtConectado) {
            List<InlineKeyboardButton> row2 = new ArrayList<>();
            InlineKeyboardButton btnEvento = new InlineKeyboardButton();
            btnEvento.setText("👉 📅 Ativar Compromissos e Consultas");
            btnEvento.setWebApp(new WebAppInfo(String.format("%s/webapp/evento-bot.html?token=%s", frontendUrl, token)));
            row2.add(btnEvento);
            rows.add(row2);
        }

        if (!cmpConectado) {
            List<InlineKeyboardButton> row3 = new ArrayList<>();
            InlineKeyboardButton btnCompra = new InlineKeyboardButton();
            btnCompra.setText("👉 🛒 Ativar Lista de Compras");
            btnCompra.setWebApp(new WebAppInfo(String.format("%s/webapp/compra-bot.html?token=%s", frontendUrl, token)));
            row3.add(btnCompra);
            rows.add(row3);
        }

        markup.setKeyboard(rows);
        message.setReplyMarkup(markup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    // Mensagem simplificada para o FAMILIAR
    private void enviarBotaoFamiliar(String chatId, String nomeUsuario, String token) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setParseMode("HTML");
        message.setText(String.format(
                "✅ Olá, <b>%s</b>!\n\nToque no botão abaixo para ativar os avisos de remédios:\n\n👇 <b>TOQUE ABAIXO</b> 👇",
                nomeUsuario
        ));

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton btnMed = new InlineKeyboardButton();
        btnMed.setText("👉 💊 Ativar Acompanhamento de Remédios");
        btnMed.setWebApp(new WebAppInfo(String.format("%s/webapp/medicamento-bot.html?token=%s", frontendUrl, token)));
        row.add(btnMed);
        rows.add(row);

        markup.setKeyboard(rows);
        message.setReplyMarkup(markup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void enviarMensagemParabensConcluido(String chatId, String nomeUsuario, Long userId, String userTipo) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setParseMode("HTML");

        StringBuilder mensagemTexto = new StringBuilder();

        if ("IDOSO".equals(userTipo)) {
            mensagemTexto.append(String.format("🎉 <b>PARABÉNS, %s!</b> 🎉\n\n", nomeUsuario));
            mensagemTexto.append("✅ <b>Tudo pronto!</b>\n");
            mensagemTexto.append("Agora você vai receber todos os seus avisos por aqui!\n\n");
            mensagemTexto.append("👇 <b>Toque abaixo para abrir o aplicativo:</b>");
        } else {
            mensagemTexto.append(String.format("🎉 <b>PARABÉNS, %s!</b> 🎉\n\n", nomeUsuario));
            mensagemTexto.append("✅ <b>Acompanhamento ativado!</b>\n");
            mensagemTexto.append("Você receberá as confirmações de remédios por aqui.\n\n");
            mensagemTexto.append("👇 <b>Toque abaixo para abrir o painel:</b>");
        }

        message.setText(mensagemTexto.toString());

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton btnProximaTela = new InlineKeyboardButton();
        btnProximaTela.setText("🚀 ABRA O APLICATIVO AQUI");

        // 💡 VALIDAÇÃO E DIRECIONAMENTO POR PERFIL:
        // ✅ CORREÇÃO: Usa o frontendUrl vindo das configurações
        String paginaDestino = "IDOSO".equalsIgnoreCase(userTipo) ? "home-idoso.html" : "home-familiar.html";
        String urlProximaTela = String.format("%s/%s?id=%d&tipo=%s", frontendUrl, paginaDestino, userId, userTipo);

        btnProximaTela.setUrl(urlProximaTela);


        row.add(btnProximaTela);
        rows.add(row);

        markup.setKeyboard(rows);
        message.setReplyMarkup(markup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void processarCallback(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();
        if (data != null && data.startsWith("TOMAR_DOSE:")) {
            Long idDose = Long.parseLong(data.split(":")[1]);
            processarConfirmacaoDose(idDose);

            AnswerCallbackQuery answer = new AnswerCallbackQuery();
            answer.setCallbackQueryId(callbackQuery.getId());
            answer.setText("✅ Muito bem! Remédio confirmado.");
            try {
                execute(answer);
            } catch (TelegramApiException ignored) {}
        }
    }

    private void processarConfirmacaoDose(Long idDose) {
        doseRepository.findById(idDose).ifPresent(dose -> {
            if (dose.getStatus() != StatusDose.TOMADO) {
                doseService.confirmarDose(idDose);
                String chatIdIdoso = dose.getMedicamento().getIdoso().getTelegramChatId();
                if (chatIdIdoso != null) {
                    notificationGateway.notificarMedicamento(chatIdIdoso, "👏 <b>Muito bem!</b> Você já tomou seu remédio.");
                }
                var familiar = dose.getMedicamento().getIdoso().getFamiliar();
                if (familiar != null && familiar.getTelegramChatId() != null) {
                    String msgFamiliar = String.format(
                            "✅ <b>%s</b> acabou de tomar o remédio: <b>%s</b>.",
                            dose.getMedicamento().getIdoso().getNome(),
                            dose.getMedicamento().getNome()
                    );
                    notificationGateway.notificarMedicamento(familiar.getTelegramChatId(), msgFamiliar);
                }
            }
        });
    }

    private void enviarMensagemSimples(String chatId, String texto) {
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId);
        msg.setParseMode("HTML");
        msg.setText(texto);
        try {
            execute(msg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}