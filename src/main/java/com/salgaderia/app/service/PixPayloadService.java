package com.salgaderia.app.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

/** Gera localmente BR Code estático por chave Pix, com valor e referência da operação. */
public final class PixPayloadService {
    public static final String CHAVE_DEMO = "uikfomi.demonstracao@example.invalid";
    private final String chave;
    private final String beneficiario;
    private final String cidade;

    public PixPayloadService(String chave, String beneficiario, String cidade) {
        this.chave = chave == null || chave.isBlank() ? CHAVE_DEMO : chave.strip();
        if (!this.chave.matches("[!-~]{1,77}")) throw new IllegalArgumentException("A chave Pix deve ter de 1 a 77 caracteres ASCII, sem espaços.");
        this.beneficiario = texto(beneficiario, "UI KFOMI SALGADERIA", 25, "beneficiário");
        this.cidade = texto(cidade, "MARANGUAPE", 15, "cidade");
    }

    public static PixPayloadService carregar() {
        Properties propriedades = new Properties();
        var loader = PixPayloadService.class.getClassLoader();
        try (var arquivo = loader.getResourceAsStream("database.local.properties")) {
            if (arquivo != null) propriedades.load(new InputStreamReader(arquivo, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível ler a configuração PIX.", e);
        }
        return new PixPayloadService(propriedades.getProperty("pix.chave"),
                propriedades.getProperty("pix.beneficiario"), propriedades.getProperty("pix.cidade"));
    }

    public boolean demonstracao() { return chave.toLowerCase(Locale.ROOT).endsWith(".invalid"); }

    public String gerarPayload(BigDecimal valor, String referencia) {
        BigDecimal exato;
        try {
            if (valor == null || valor.signum() <= 0) throw new ArithmeticException();
            exato = valor.setScale(2, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException e) { throw new IllegalArgumentException("Informe um valor PIX positivo, com até duas casas decimais."); }
        if (exato.compareTo(new BigDecimal("9999999999.99")) > 0) throw new IllegalArgumentException("Valor PIX acima do limite do BR Code.");
        String txid = referencia == null || referencia.isBlank() ? "***" : referencia;
        if (!txid.equals("***") && !txid.matches("[a-zA-Z0-9]{1,25}")) throw new IllegalArgumentException("Referência PIX inválida.");
        String campos = tlv("00", "01")
                + tlv("26", tlv("00", "br.gov.bcb.pix") + tlv("01", chave))
                + tlv("52", "0000") + tlv("53", "986") + tlv("54", exato.toPlainString())
                + tlv("58", "BR") + tlv("59", beneficiario) + tlv("60", cidade)
                + tlv("62", tlv("05", txid)) + "6304";
        return campos + crc16(campos);
    }

    public WritableImage gerarImagem(String payload, int lado) {
        if (lado < 96 || lado > 2048 || payload == null || payload.isBlank())
            throw new IllegalArgumentException("Imagem PIX inválida.");
        try {
            var matriz = new QRCodeWriter().encode(payload, BarcodeFormat.QR_CODE, lado, lado,
                    Map.of(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M, EncodeHintType.MARGIN, 4));
            var qr = MatrixToImageWriter.toBufferedImage(matriz);
            var imagem = new WritableImage(qr.getWidth(), qr.getHeight());
            int[] pixels = qr.getRGB(0, 0, qr.getWidth(), qr.getHeight(), null, 0, qr.getWidth());
            imagem.getPixelWriter().setPixels(0, 0, qr.getWidth(), qr.getHeight(),
                    PixelFormat.getIntArgbInstance(), pixels, 0, qr.getWidth());
            return imagem;
        } catch (WriterException e) { throw new IllegalStateException("Não foi possível gerar o QR Code PIX.", e); }
    }

    private static String texto(String valor, String padrao, int limite, String campo) {
        String original = valor == null || valor.isBlank() ? padrao : valor.strip();
        String normalizado = Normalizer.normalize(original, Normalizer.Form.NFD).replaceAll("\\p{M}+", "")
                .toUpperCase(Locale.ROOT).replaceAll(" +", " ");
        if (normalizado.length() > limite || !normalizado.matches("[A-Z0-9 .&'/-]+"))
            throw new IllegalArgumentException("Confira " + campo + " na configuração PIX (até " + limite + " caracteres).");
        return normalizado;
    }

    private static String tlv(String id, String valor) {
        int tamanho = valor.getBytes(StandardCharsets.UTF_8).length;
        if (tamanho > 99) throw new IllegalArgumentException("Campo BR Code acima de 99 bytes.");
        return id + String.format(Locale.ROOT, "%02d", tamanho) + valor;
    }

    private static String crc16(String campos) {
        int crc = 0xffff;
        for (byte b : campos.getBytes(StandardCharsets.UTF_8)) {
            crc ^= (b & 0xff) << 8;
            for (int bit = 0; bit < 8; bit++)
                crc = ((crc & 0x8000) != 0 ? (crc << 1) ^ 0x1021 : crc << 1) & 0xffff;
        }
        return String.format(Locale.ROOT, "%04X", crc);
    }
}
