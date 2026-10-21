package com.auth.simpleauth.service;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class QrCodeService {

    public byte[] gerarQrCodePorId(Long idosoId, int largura, int altura) throws Exception {
        // Formata os dados que estarão contidos no QR Code (ex: JSON simples com o ID)
        String conteudo = String.format("{\"sistema\":\"VITALIS\",\"tipo\":\"VINCULO\",\"idosoId\":%d}", idosoId);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        // Gera a matriz de pontos pretos e brancos
        BitMatrix bitMatrix = qrCodeWriter.encode(conteudo, BarcodeFormat.QR_CODE, largura, altura);

        // Converte a matriz de pontos em uma imagem PNG em memória (array de bytes)
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

        return outputStream.toByteArray();
    }
}
