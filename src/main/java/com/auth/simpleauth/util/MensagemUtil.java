package com.auth.simpleauth.util;

import com.auth.simpleauth.entity.Dose;
import com.auth.simpleauth.entity.Medicamento;

import java.time.format.DateTimeFormatter;

public class MensagemUtil {

    private MensagemUtil() {}

    public static String montarMensagemAcessivel(Dose dose) {
        Medicamento med = dose.getMedicamento();

        String horarioFormatado = dose.getHorarioProgramado().format(DateTimeFormatter.ofPattern("HH:mm")) + "h";
        String emojiCor = obterEmojiCor(med.getCorCaixa());

        String quantidadeDose = (med.getQuantidadePorDose() % 1 == 0)
                ? String.valueOf(med.getQuantidadePorDose().longValue())
                : String.valueOf(med.getQuantidadePorDose());

        StringBuilder msg = new StringBuilder();
        msg.append("🔔 <b>HORA DO REMÉDIO!</b>\n\n");
        msg.append(String.format("⏰ <b>Horário: %s</b>\n", horarioFormatado));


        if (med.getCorCaixa() != null || med.getIdentificadorCaixa() != null) {
            String identificador = med.getIdentificadorCaixa() != null ? med.getIdentificadorCaixa().trim() : "";
            boolean isNumero = identificador.matches("\\d+");
            String tipoIdentificador = isNumero ? "com o número" : "com a letra";

            msg.append(String.format("%s <b>Procure a caixa %s %s %s</b>\n\n",
                    emojiCor,
                    med.getCorCaixa() != null ? med.getCorCaixa().toUpperCase() : "",
                    tipoIdentificador,
                    identificador));
        }

        msg.append(String.format("💊 <i>Remédio: %s (%s %s)</i>",
                med.getNome(),
                quantidadeDose,
                med.getUnidade().name().toLowerCase()));

        return msg.toString();
    }

    public static String obterEmojiCor(String cor) {
        if (cor == null) return "📦";
        return switch (cor.toUpperCase().trim()) {
            case "VERMELHO" -> "🟥";
            case "LARANJA"  -> "🟧";
            case "AMARELO"  -> "🟨";
            case "VERDE"    -> "🟩";
            case "AZUL"     -> "🟦";
            case "ROXO"     -> "🟪";
            case "MARROM"   -> "🟫";
            case "BRANCO"   -> "⬜";
            case "PRETO"    -> "⬛";
            default         -> "📦";
        };
    }
}