package com.salgaderia.app.model;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/** Metadados compartilhados pelas linhas reais de estoque de um combo ou caixa. */
public record PersonalizacaoItem(UUID grupo, Tipo tipo, Set<Restricao> restricoes) {
    public PersonalizacaoItem {
        Objects.requireNonNull(grupo);
        Objects.requireNonNull(tipo);
        restricoes = Set.copyOf(restricoes);
        if (tipo == Tipo.COMBO && !restricoes.isEmpty())
            throw new IllegalArgumentException("Restrições são usadas apenas na caixa misteriosa.");
    }

    public enum Tipo {
        COMBO("Monte seu Combo", 0), FOME_LEVE("Fome Leve", 10), FOME_DE_LEAO("Fome de Leão", 25);

        private final String rotulo;
        private final int unidades;
        Tipo(String rotulo, int unidades) { this.rotulo = rotulo; this.unidades = unidades; }
        public int unidades() { return unidades; }
        public boolean caixa() { return unidades > 0; }
        @Override public String toString() { return caixa() ? rotulo + " (" + unidades + " un.)" : rotulo; }
    }

    public enum Restricao {
        SEM_QUEIJO("Sem queijo"), SEM_CALABRESA("Sem calabresa"), SO_ASSADOS("Só assados");

        private final String rotulo;
        Restricao(String rotulo) { this.rotulo = rotulo; }
        @Override public String toString() { return rotulo; }
    }
}
