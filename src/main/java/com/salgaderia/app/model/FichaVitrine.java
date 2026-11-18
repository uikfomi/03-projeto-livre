package com.salgaderia.app.model;

import java.text.Normalizer;
import java.util.Locale;

/** Informações do cardápio confirmadas no cadastro do produto. */
public record FichaVitrine(TipoCombo tipo, String recheio, Boolean contemQueijo, Boolean contemCalabresa, boolean assado) {
    public static final FichaVitrine VAZIA = new FichaVitrine(null, null, null, null, false);

    public FichaVitrine {
        recheio = recheio == null || recheio.isBlank() ? null : recheio.strip();
        if (tipo == null || tipo == TipoCombo.ACOMPANHAMENTO) recheio = null;
        else if (recheio == null || recheio.length() > 100)
            throw new IllegalArgumentException("Informe o recheio da base do combo (até 100 caracteres).");
    }

    public enum TipoCombo {
        COXINHA("Coxinha"), PASTEL("Pastel"), EMPADA("Empada"), ESFIHA("Esfiha"),
        ACOMPANHAMENTO("Molho / acompanhamento");

        private final String rotulo;
        TipoCombo(String rotulo) { this.rotulo = rotulo; }
        @Override public String toString() { return rotulo; }
    }

    public static boolean categoriaSalgado(Categoria categoria) {
        if (categoria == null) return false;
        String nome = Normalizer.normalize(categoria.getNome(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "").toLowerCase(Locale.ROOT);
        return !nome.contains("bebid") && !nome.contains("doc") &&
                (nome.contains("salgad") || nome.contains("frit") || nome.contains("assad"));
    }
}
