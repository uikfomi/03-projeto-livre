package com.salgaderia.app.service;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

/** Armazena somente fotos locais validadas; a referência do banco não é uma URL remota. */
public final class FotoAvaliacaoStorage {
    private static final String PREFIXO = "uploads/avaliacoes/";
    private static final Pattern REFERENCIA = Pattern.compile(PREFIXO + "[0-9a-f]{8}(?:-[0-9a-f]{4}){3}-[0-9a-f]{12}\\.png");
    private static final int LIMITE_BYTES = 8 * 1024 * 1024;
    private static final int LIMITE_PIXELS = 12_000_000;
    private static final int MAX_LADO = 1280;
    private final Path pasta;

    public FotoAvaliacaoStorage() { this(Path.of(PREFIXO)); }
    public FotoAvaliacaoStorage(Path pasta) { this.pasta = pasta.toAbsolutePath().normalize(); }

    public String salvar(Path origem) throws IOException {
        if (!Files.isRegularFile(origem, LinkOption.NOFOLLOW_LINKS))
            throw new IllegalArgumentException("Selecione um arquivo de imagem válido.");
        byte[] conteudo;
        try (InputStream entrada = Files.newInputStream(origem, LinkOption.NOFOLLOW_LINKS)) {
            conteudo = entrada.readNBytes(LIMITE_BYTES + 1);
        }
        if (conteudo.length > LIMITE_BYTES) throw new IllegalArgumentException("Selecione uma foto de até 8 MB.");
        BufferedImage foto;
        try {
            foto = decodificar(conteudo);
        } catch (IOException erro) {
            throw new IllegalArgumentException("A foto está corrompida ou não é JPEG/PNG.", erro);
        }
        if (pastasSaoLinks()) throw new IOException("A pasta de avaliações não pode ser um link simbólico.");
        Files.createDirectories(pasta);
        String nome = UUID.randomUUID() + ".png";
        Path destino = pasta.resolve(nome);
        var saida = Files.newOutputStream(destino, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
        try (saida) {
            if (!ImageIO.write(foto, "png", saida)) throw new IOException("Codificador PNG indisponível.");
        } catch (IOException | RuntimeException erro) {
            try { Files.deleteIfExists(destino); } catch (IOException limpeza) { erro.addSuppressed(limpeza); }
            throw erro;
        }
        return PREFIXO + nome;
    }

    private static BufferedImage decodificar(byte[] conteudo) throws IOException {
        try (var stream = new javax.imageio.stream.MemoryCacheImageInputStream(new ByteArrayInputStream(conteudo))) {
            var leitores = ImageIO.getImageReaders(stream);
            if (!leitores.hasNext()) throw new IllegalArgumentException("Selecione uma foto JPEG ou PNG.");
            var leitor = leitores.next();
            try {
                String formato = leitor.getFormatName().toLowerCase(Locale.ROOT);
                if (!formato.equals("jpeg") && !formato.equals("png")) throw new IllegalArgumentException("Selecione uma foto JPEG ou PNG.");
                leitor.setInput(stream, true, true);
                int largura = leitor.getWidth(0), altura = leitor.getHeight(0);
                if (largura <= 0 || altura <= 0 || (long) largura * altura > LIMITE_PIXELS)
                    throw new IllegalArgumentException("Selecione uma foto de até 12 megapixels.");
                double escala = Math.min(1.0, (double) MAX_LADO / Math.max(largura, altura));
                var foto = new BufferedImage(Math.max(1, (int) (largura * escala)), Math.max(1, (int) (altura * escala)),
                        BufferedImage.TYPE_INT_ARGB);
                Graphics2D grafico = foto.createGraphics();
                try {
                    grafico.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    grafico.drawImage(leitor.read(0), 0, 0, foto.getWidth(), foto.getHeight(), null);
                } finally { grafico.dispose(); }
                return foto;
            } finally { leitor.dispose(); }
        }
    }

    private boolean pastasSaoLinks() {
        return Files.isSymbolicLink(pasta) || (pasta.getParent() != null && Files.isSymbolicLink(pasta.getParent()));
    }

    public Optional<Path> resolver(String referencia) {
        if (referencia == null || !REFERENCIA.matcher(referencia).matches() || pastasSaoLinks()) return Optional.empty();
        Path arquivo = pasta.resolve(referencia.substring(PREFIXO.length()));
        return Files.isRegularFile(arquivo, LinkOption.NOFOLLOW_LINKS) ? Optional.of(arquivo) : Optional.empty();
    }

    public void excluir(String referencia) throws IOException {
        Optional<Path> arquivo = resolver(referencia);
        if (arquivo.isPresent()) Files.deleteIfExists(arquivo.get());
    }
}
