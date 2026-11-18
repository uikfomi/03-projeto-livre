package com.salgaderia.app.service;

import com.salgaderia.app.model.*;
import com.salgaderia.app.model.FichaVitrine.TipoCombo;
import com.salgaderia.app.model.PersonalizacaoItem.Tipo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/** Monta a receita com a variante real da massa/recheio e um acompanhamento opcional. */
public final class ComboBuilder {
    private TipoCombo massa;
    private Produto variante;
    private Produto acompanhamento;

    public ComboBuilder massa(TipoCombo massa) { this.massa = massa; return this; }
    public ComboBuilder recheio(Produto variante) { this.variante = variante; return this; }
    public ComboBuilder acompanhamento(Produto acompanhamento) { this.acompanhamento = acompanhamento; return this; }

    public List<PedidoItem> montar() {
        if (massa == null || massa == TipoCombo.ACOMPANHAMENTO || variante == null ||
                variante.getFichaVitrine().tipo() != massa)
            throw new IllegalArgumentException("Selecione uma massa e um recheio do cardápio.");
        var personalizacao = new PersonalizacaoItem(UUID.randomUUID(), Tipo.COMBO, Set.of());
        List<PedidoItem> itens = new ArrayList<>();
        itens.add(componente(variante, personalizacao));
        if (acompanhamento != null) itens.add(componente(acompanhamento, personalizacao));
        return PersonalizacaoService.descrever(itens);
    }

    private static PedidoItem componente(Produto produto, PersonalizacaoItem personalizacao) {
        Objects.requireNonNull(produto.getId(), "Produto sem cadastro");
        return new PedidoItem(null, produto, BigDecimal.ONE, produto.getPrecoVenda(), null, personalizacao);
    }
}
