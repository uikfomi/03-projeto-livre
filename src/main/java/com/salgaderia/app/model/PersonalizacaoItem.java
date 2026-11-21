package com.salgaderia.app.model;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/** Metadados compartilhados pelos itens reais de um combo ou caixa. */
public record PersonalizacaoItem(UUID grupo, Tipo tipo, Set<Restricao> restricoes,
                                 Set<Long> categoriasExcluidas, Set<Long> produtosExcluidos) {
    public PersonalizacaoItem(UUID grupo,Tipo tipo,Set<Restricao> restricoes) {
        this(grupo,tipo,restricoes,Set.of(),Set.of());
    }
    public PersonalizacaoItem {
        Objects.requireNonNull(grupo);
        Objects.requireNonNull(tipo);
        restricoes=Set.copyOf(restricoes);
        categoriasExcluidas=Set.copyOf(categoriasExcluidas);
        produtosExcluidos=Set.copyOf(produtosExcluidos);
        if(tipo.orcamento()==null&&(!categoriasExcluidas.isEmpty()||!produtosExcluidos.isEmpty()))
            throw new IllegalArgumentException("Exclusões são usadas apenas nas caixas por orçamento.");
        if(!tipo.legado()&&!restricoes.isEmpty())
            throw new IllegalArgumentException("Escolha exclusões pelo cardápio.");
    }

    public enum Tipo {
        COMBO("Monte seu Combo",null,true), FOME_LEVE("Fome Leve (10 un.)",null,true),
        FOME_DE_LEAO("Fome de Leão (25 un.)",null,true), COMBO_SIMPLES("Monte seu Combo",null,false),
        CAIXA_25("R$ 25,00",25,false), CAIXA_40("R$ 40,00",40,false), CAIXA_60("R$ 60,00",60,false);

        private final String rotulo;
        private final BigDecimal orcamento;
        private final boolean legado;
        Tipo(String rotulo,Integer orcamento,boolean legado){
            this.rotulo=rotulo;this.orcamento=orcamento==null?null:BigDecimal.valueOf(orcamento).setScale(2);this.legado=legado;
        }
        public BigDecimal orcamento(){return orcamento;}
        public boolean legado(){return legado;}
        public boolean caixa(){return orcamento!=null||this==FOME_LEVE||this==FOME_DE_LEAO;}
        @Override public String toString(){return rotulo;}
    }

    /** Valores mantidos para ler pedidos históricos. */
    public enum Restricao { SEM_QUEIJO, SEM_CALABRESA, SO_ASSADOS }
}
