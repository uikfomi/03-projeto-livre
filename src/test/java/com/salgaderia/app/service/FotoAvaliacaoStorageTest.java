package com.salgaderia.app.service;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;

class FotoAvaliacaoStorageTest {
    @TempDir Path temporario;

    @Test void armazenaImagemLocalComNomeProprioEExcluiSomenteACopia() throws Exception {
        Path original = temporario.resolve("foto original.jpg");
        var imagem = new BufferedImage(1600, 800, BufferedImage.TYPE_INT_RGB);
        imagem.setRGB(100, 100, Color.RED.getRGB());
        assertTrue(ImageIO.write(imagem, "jpg", original.toFile()));
        var storage = new FotoAvaliacaoStorage(temporario.resolve("uploads/avaliacoes"));
        String referencia = storage.salvar(original);
        assertTrue(referencia.matches("uploads/avaliacoes/[0-9a-f-]+\\.png"));
        Path copia = storage.resolver(referencia).orElseThrow();
        var foto = ImageIO.read(copia.toFile());
        assertEquals(1280, foto.getWidth());
        assertEquals(640, foto.getHeight());
        assertTrue(storage.resolver("https://example.org/foto.png").isEmpty());
        assertTrue(storage.resolver("uploads/avaliacoes/../../foto original.jpg").isEmpty());
        storage.excluir(referencia);
        assertTrue(storage.resolver(referencia).isEmpty());
        assertTrue(Files.exists(original));
    }

    @Test void arquivoComExtensaoFalsaOuTamanhoExcessivoNaoCriaUpload() throws Exception {
        Path falsa = Files.writeString(temporario.resolve("arquivo.png"), "não é uma imagem");
        Path grande = Files.write(temporario.resolve("grande.png"), new byte[8 * 1024 * 1024 + 1]);
        Path destino = temporario.resolve("avaliacoes");
        FotoAvaliacaoStorage storage = new FotoAvaliacaoStorage(destino);
        assertThrows(IllegalArgumentException.class, () -> storage.salvar(falsa));
        assertThrows(IllegalArgumentException.class, () -> storage.salvar(grande));
        assertFalse(Files.exists(destino));
    }
}
