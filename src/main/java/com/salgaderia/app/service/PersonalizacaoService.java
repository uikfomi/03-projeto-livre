package com.salgaderia.app.service;

import com.salgaderia.app.model.*;
import com.salgaderia.app.model.FichaVitrine.TipoCombo;
import com.salgaderia.app.model.PersonalizacaoItem.Restricao;
import com.salgaderia.app.model.PersonalizacaoItem.Tipo;

import java.math.BigDecimal;
import java.util.*;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;

/** Regras do combo, seleção da caixa e disponibilidade compartilhada entre linhas do carrinho. */
public final class PersonalizacaoService {
    private final RandomGenerator sorteio;
    public PersonalizacaoService() { this(RandomGenerator.getDefault()); }
    public PersonalizacaoService(RandomGenerator sorteio) { this.sorteio = Objects.requireNonNull(sorteio); }

    public static Map<Long, BigDecimal> quantidades(List<PedidoItem> itens) {
        Map<Long, BigDecimal> quantidades = new LinkedHashMap<>();
        for (PedidoItem i : itens) quantidades.merge(i.getProduto().getId(), i.getQuantidade(), BigDecimal::add);
        return quantidades;
    }

    public List<PedidoItem> sortearCaixa(Tipo tipo, Set<Restricao> restricoes, List<Produto> catalogo) {
        if (tipo == null || !tipo.caixa()) throw new IllegalArgumentException("Selecione um tamanho de caixa.");
        PersonalizacaoItem personalizacao = new PersonalizacaoItem(UUID.randomUUID(), tipo, restricoes);
        List<Produto> candidatos = catalogo.stream().filter(p -> p.getId() != null && p.isDisponivelVenda()
                && elegivel(p, personalizacao.restricoes())).toList();
        if (candidatos.isEmpty())
            throw new IllegalArgumentException("Não há salgados disponíveis para esta caixa com as restrições selecionadas.");
        Map<Produto, Integer> selecionados = new LinkedHashMap<>();
        for (int i = 0; i < tipo.unidades(); i++)
            selecionados.merge(candidatos.get(sorteio.nextInt(candidatos.size())), 1, Integer::sum);
        return descrever(selecionados.entrySet().stream()
                .map(e -> new PedidoItem(null, e.getKey(), BigDecimal.valueOf(e.getValue()),
                        e.getKey().getPrecoVenda(), null, personalizacao)).toList());
    }

    private static boolean elegivel(Produto p, Set<Restricao> restricoes) {
        FichaVitrine ficha = p.getFichaVitrine();
        boolean salgado = ficha.tipo() != TipoCombo.ACOMPANHAMENTO &&
                (ficha.tipo() != null || FichaVitrine.categoriaSalgado(p.getCategoria()));
        return salgado
                && (!restricoes.contains(Restricao.SEM_QUEIJO) || Boolean.FALSE.equals(ficha.contemQueijo()))
                && (!restricoes.contains(Restricao.SEM_CALABRESA) || Boolean.FALSE.equals(ficha.contemCalabresa()))
                && (!restricoes.contains(Restricao.SO_ASSADOS) || ficha.assado());
    }

    public static Map<UUID, List<PedidoItem>> grupos(List<PedidoItem> itens) {
        return itens.stream().filter(i -> i.getPersonalizacao() != null)
                .collect(Collectors.groupingBy(i -> i.getPersonalizacao().grupo(), LinkedHashMap::new, Collectors.toList()));
    }

    private static String descricao(List<PedidoItem> grupo) {
        PersonalizacaoItem personalizacao = grupo.get(0).getPersonalizacao();
        if (grupo.stream().anyMatch(i -> !personalizacao.equals(i.getPersonalizacao()) ||
                i.getQuantidade().stripTrailingZeros().scale() > 0))
            throw new IllegalArgumentException("Composição do combo ou da caixa inválida.");
        if (personalizacao.tipo().caixa()) {
            BigDecimal quantidade = grupo.stream().map(PedidoItem::getQuantidade).reduce(BigDecimal.ZERO, BigDecimal::add);
            if (quantidade.compareTo(BigDecimal.valueOf(personalizacao.tipo().unidades())) != 0 ||
                    grupo.stream().anyMatch(i -> !elegivel(i.getProduto(), personalizacao.restricoes())))
                throw new IllegalArgumentException("A caixa não atende ao tamanho ou às restrições. Monte a caixa novamente.");
            String restricoes = personalizacao.restricoes().stream().sorted().map(Restricao::toString)
                    .collect(Collectors.joining(", "));
            return "Caixa Misteriosa — " + personalizacao.tipo() + (restricoes.isEmpty() ? "" : " · " + restricoes);
        }
        List<PedidoItem> bases = grupo.stream().filter(i -> {
            TipoCombo tipo = i.getProduto().getFichaVitrine().tipo();
            return tipo != null && tipo != TipoCombo.ACOMPANHAMENTO;
        }).toList();
        if (bases.size() != 1 || grupo.size() > 2 || grupo.stream().anyMatch(i ->
                i.getQuantidade().compareTo(BigDecimal.ONE) != 0 || i.getProduto().getFichaVitrine().tipo() == null))
            throw new IllegalArgumentException("O combo exige uma base com recheio e até um acompanhamento.");
        PedidoItem base = bases.get(0);
        FichaVitrine ficha = base.getProduto().getFichaVitrine();
        return "Combo — " + ficha.tipo() + " com " + ficha.recheio() +
                grupo.stream().filter(i -> i != base).findFirst()
                        .map(i -> " + " + i.getProduto().getNome()).orElse("");
    }

    public static List<PedidoItem> descrever(List<PedidoItem> itens) {
        Map<UUID, String> descricoes = new HashMap<>();
        grupos(itens).forEach((id, grupo) -> descricoes.put(id, descricao(grupo)));
        return itens.stream().map(i -> i.getPersonalizacao() == null ? i :
                new PedidoItem(null, i.getProduto(), i.getQuantidade(), i.getPrecoUnitario(),
                        descricoes.get(i.getPersonalizacao().grupo()), i.getPersonalizacao())).toList();
    }

    public static void validarComposicao(List<PedidoItem> itens) {
        grupos(itens).values().forEach(grupo -> {
            String descricao = descricao(grupo);
            if (grupo.stream().anyMatch(i -> !descricao.equals(i.getObservacaoPersonalizada())))
                throw new IllegalArgumentException("A ficha do combo mudou. Confira e monte o combo novamente.");
        });
    }
}
