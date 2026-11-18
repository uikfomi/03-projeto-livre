package com.salgaderia.app.service;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PixPayloadServiceTest {
    @Test void payloadTemValorExatoEChecksumDaReferencia() {
        var pix=new PixPayloadService(null,null,null);
        String payload=pix.gerarPayload(new BigDecimal("123.450"),"P105");
        assertEquals("00020126580014br.gov.bcb.pix0136uikfomi.demonstracao@example.invalid5204000053039865406123.455802BR5919UI KFOMI SALGADERIA6010MARANGUAPE62080504P105630463AB",payload);
        assertTrue(pix.demonstracao());
        assertThrows(IllegalArgumentException.class,()->pix.gerarPayload(BigDecimal.ZERO,null));
        assertThrows(IllegalArgumentException.class,()->pix.gerarPayload(new BigDecimal("1.234"),null));
        assertThrows(IllegalArgumentException.class,()->pix.gerarPayload(BigDecimal.ONE,"inválido!"));
        assertThrows(IllegalArgumentException.class,()->new PixPayloadService("chave com espaço",null,null));
    }

    @Test void imagemCodificaOMesmoBrCodeUsadoPeloCopiaECola() throws Exception {
        var pix=new PixPayloadService(null,"Ui K'Fomi","Maranguape");
        String payload=pix.gerarPayload(new BigDecimal("9.95"),null);
        assertTrue(payload.contains("54049.95"));
        assertTrue(payload.contains("62070503***"));
        var imagem=pix.gerarImagem(payload,208);
        int largura=(int)imagem.getWidth(),altura=(int)imagem.getHeight();
        BufferedImage buffer=new BufferedImage(largura,altura,BufferedImage.TYPE_INT_RGB);
        var leitor=imagem.getPixelReader();
        for(int x=0;x<largura;x++)for(int y=0;y<altura;y++)buffer.setRGB(x,y,leitor.getArgb(x,y));
        var bitmap=new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(buffer)));
        assertEquals(payload,new QRCodeReader().decode(bitmap).getText());
    }
}
