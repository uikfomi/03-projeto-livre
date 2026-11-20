package com.salgaderia.app.service;

import com.salgaderia.app.exception.ProdutoIndisponivelException;
import com.salgaderia.app.model.*;
import com.salgaderia.app.model.FichaVitrine.TipoCombo;
import com.salgaderia.app.model.PersonalizacaoItem.Restricao;
import com.salgaderia.app.model.PersonalizacaoItem.Tipo;
import com.salgaderia.app.service.desconto.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PersonalizacaoServiceTest {
    private static final Categoria SALGADOS = new Categoria(1L, "Salgados Assados", null);

    @Test void comboUsaVarianteRealPrecoAtualEUmGrupoUnico() {
        Produto base = produto(1, "Empada de frango", "8.00", "2", SALGADOS,
                new FichaVitrine(TipoCombo.EMPADA, "Frango", false, false, true));
        Produto molho = produto(2, "Molho da casa", "1.50", "1", null,
                new FichaVitrine(TipoCombo.ACOMPANHAMENTO, null, null, null, false));
        List<PedidoItem> itens = new ComboBuilder().massa(TipoCombo.EMPADA).recheio(base).acompanhamento(molho).montar();
        assertEquals(List.of(base, molho), itens.stream().map(PedidoItem::getProduto).toList());
        assertEquals(new BigDecimal("9.50"), new ContextoDesconto(itens, 0, null).subtotal());
        assertEquals("Combo — Empada com Frango + Molho da casa", itens.get(0).getObservacaoPersonalizada());
        assertEquals(itens.get(0).getPersonalizacao(), itens.get(1).getPersonalizacao());
        PersonalizacaoService.validarComposicao(itens);
        assertThrows(IllegalArgumentException.class, () -> new ComboBuilder().massa(TipoCombo.COXINHA).recheio(base).montar());
        assertThrows(IllegalArgumentException.class, () -> new ComboBuilder().massa(TipoCombo.EMPADA).recheio(base).acompanhamento(base).montar());
        base.setEstoqueAtual(BigDecimal.ZERO);
        assertDoesNotThrow(base::exigirDisponivelParaVenda);
        base.setDisponivelVenda(false);
        assertThrows(ProdutoIndisponivelException.class,base::exigirDisponivelParaVenda);
    }

    @Test void sorteioRespeitaRestricoesBloqueioEQuantidadeExata() {
        Produto primeiro = produto(1, "Empada", "3.50", "0", SALGADOS,
                new FichaVitrine(TipoCombo.EMPADA, "Frango", false, false, true));
        Produto segundo = produto(2, "Esfiha", "3.50", "4", SALGADOS,
                new FichaVitrine(TipoCombo.ESFIHA, "Carne", false, false, true));
        Produto desconhecido = produto(3, "Ficha incompleta", "3.50", "100", SALGADOS,
                new FichaVitrine(null, null, null, false, true));
        Produto queijo = produto(4, "Com queijo", "3.50", "100", SALGADOS,
                new FichaVitrine(null, null, true, false, true));
        Produto calabresa = produto(5, "Com calabresa", "3.50", "100", SALGADOS,
                new FichaVitrine(null, null, false, true, true));
        Produto frito = produto(6, "Frito", "3.50", "100", SALGADOS,
                new FichaVitrine(null, null, false, false, false));
        Produto bebida = produto(7, "Bebida", "3.50", "100", new Categoria(2L, "Bebidas", null),
                new FichaVitrine(null, null, false, false, true));
        segundo.setDisponivelVenda(false);
        List<Produto> catalogo = List.of(primeiro, segundo, desconhecido, queijo, calabresa, frito, bebida);
        Set<Restricao> restricoes = Set.of(Restricao.SEM_QUEIJO, Restricao.SEM_CALABRESA, Restricao.SO_ASSADOS);
        var servico = new PersonalizacaoService(new Random(25));
        List<PedidoItem> itens = servico.sortearCaixa(Tipo.FOME_LEVE, restricoes, catalogo);
        assertEquals(Map.of(1L, BigDecimal.TEN), PersonalizacaoService.quantidades(itens));
        assertTrue(itens.get(0).getObservacaoPersonalizada().contains("Sem calabresa"));
        PersonalizacaoService.validarComposicao(itens);
        assertEquals(Map.of(1L,BigDecimal.valueOf(25)),PersonalizacaoService.quantidades(servico.sortearCaixa(Tipo.FOME_DE_LEAO,restricoes,catalogo)));
        primeiro.setDisponivelVenda(false);
        assertThrows(IllegalArgumentException.class, () -> servico.sortearCaixa(Tipo.FOME_DE_LEAO, restricoes, catalogo));
        primeiro.setFichaVitrine(new FichaVitrine(TipoCombo.EMPADA, "Frango com queijo", true, false, true));
        assertThrows(IllegalArgumentException.class, () -> PersonalizacaoService.validarComposicao(itens));
    }

    @Test void maiorDescontoUnicoIncluiOPrecoFechadoDaCaixa() {
        Produto produto = produto(1, "Empada", "3.33", "25", SALGADOS, FichaVitrine.VAZIA);
        List<PedidoItem> caixa = new PersonalizacaoService(new Random(2)).sortearCaixa(Tipo.FOME_DE_LEAO, Set.of(), List.of(produto));
        assertEquals(BigDecimal.valueOf(25), caixa.get(0).getQuantidade());
        var calculadora = new CalculadoraDescontos();
        ResultadoDesconto desconto = calculadora.calcular(new ContextoDesconto(caixa, 4, new Cupom("UIKFOMI10", BigDecimal.TEN, BigDecimal.ZERO)));
        assertEquals("CAIXA_MISTERIOSA", desconto.estrategia());
        assertEquals(new BigDecimal("12.49"), desconto.desconto());
        assertEquals(new BigDecimal("70.76"), desconto.total());
        List<PedidoItem> misto = List.of(caixa.get(0), new PedidoItem(null, produto, new BigDecimal("20"), produto.getPrecoVenda()));
        desconto = calculadora.calcular(new ContextoDesconto(misto, 4, new Cupom("UIKFOMI10", BigDecimal.TEN, BigDecimal.ZERO)));
        assertEquals("CUPOM", desconto.estrategia());
        assertEquals(new BigDecimal("14.99"), desconto.desconto());
    }

    private Produto produto(long id, String nome, String preco, String estoque, Categoria categoria, FichaVitrine ficha) {
        Produto produto = new Produto(id, categoria, nome, null, new BigDecimal(preco), BigDecimal.ONE, new BigDecimal(estoque), BigDecimal.ONE, null);
        produto.setFichaVitrine(ficha);
        return produto;
    }
}
