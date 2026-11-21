package com.salgaderia.app.service;

import com.salgaderia.app.exception.ProdutoIndisponivelException;
import com.salgaderia.app.model.*;
import com.salgaderia.app.model.PersonalizacaoItem.Tipo;
import com.salgaderia.app.service.desconto.*;
import java.math.BigDecimal;
import java.util.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PersonalizacaoServiceTest {
    private static final Categoria BEBIDAS=new Categoria(7L,"Bebidas",null);
    private static final Categoria FRITOS=new Categoria(8L,"Fritos",null);

    @Test void comboSemFichaTecnicaUsaTresProdutosEDescontoUnico() {
        Produto principal=produto(1,"Empada","8.00","2.00",null);
        Produto acompanhamento=produto(2,"Molho","2.00","1.00",null);
        Produto bebida=produto(3,"Suco","5.00","1.00",BEBIDAS);
        var builder=new ComboBuilder().principal(principal).acompanhamento(acompanhamento).bebida(bebida);
        List<PedidoItem> itens=builder.montar();
        assertEquals(List.of(principal,acompanhamento,bebida),itens.stream().map(PedidoItem::getProduto).toList());
        assertEquals(1,PersonalizacaoService.grupos(itens).size());
        PersonalizacaoService.validarComposicao(itens);
        var resultado=new CalculadoraDescontos().calcular(new ContextoDesconto(itens,3,null));
        assertEquals("COMBO",resultado.estrategia());assertEquals(new BigDecimal("13.50"),resultado.total());
        assertThrows(IllegalArgumentException.class,()->builder.acompanhamento(principal).montar());
        assertThrows(IllegalArgumentException.class,()->builder.acompanhamento(acompanhamento).bebida(acompanhamento).montar());
        acompanhamento.setDisponivelVenda(false);
        assertThrows(ProdutoIndisponivelException.class,()->builder.bebida(bebida).montar());
    }

    @Test void sorteioSeguroEmTodosOrcamentosComCategoriasEItensExcluidos() {
        Produto barato=produto(1,"Coxinha","5.00","1.00",FRITOS);
        Produto outro=produto(2,"Empada","6.00","2.00",null);
        Produto excluir=produto(3,"Pastel","5.00","1.00",FRITOS);
        Produto bebida=produto(4,"Suco","5.00","1.00",BEBIDAS);
        Produto prejuizo=produto(5,"Item com custo superior ao orçamento","26.00","61.00",null);
        Produto bloqueado=produto(6,"Produto bloqueado","5.00","1.00",null);bloqueado.setDisponivelVenda(false);
        var catalogo=List.of(barato,outro,excluir,bebida,prejuizo,bloqueado);
        for(Tipo tipo:List.of(Tipo.CAIXA_25,Tipo.CAIXA_40,Tipo.CAIXA_60))for(int seed=0;seed<20;seed++){
            var itens=new PersonalizacaoService(new Random(seed)).sortearCaixa(tipo,catalogo,Set.of(7L),Set.of(3L));
            assertTrue(itens.stream().allMatch(i->Set.of(barato,outro).contains(i.getProduto())));
            assertTrue(PersonalizacaoService.subtotal(itens).compareTo(tipo.orcamento())>=0);
            assertTrue(PersonalizacaoService.subtotal(itens).compareTo(tipo.orcamento().multiply(new BigDecimal("1.15")))<=0);
            assertTrue(itens.stream().map(i->i.getProduto().getCustoProducao().multiply(i.getQuantidade())).reduce(BigDecimal.ZERO,BigDecimal::add)
                    .compareTo(tipo.orcamento())<=0);
            PersonalizacaoService.validarComposicao(itens);
        }
    }

    @Test void custoSomadoInvalidaCaixaQueCaberiaPeloPrecoDeCatalogo() {
        var produto=produto(1,"Empada","13.00","12.51",null);
        assertThrows(IllegalArgumentException.class,()->new PersonalizacaoService().sortearCaixa(Tipo.CAIXA_25,List.of(produto),Set.of(),Set.of()));
        produto.setCustoProducao(new BigDecimal("12.50"));
        var itens=new PersonalizacaoService().sortearCaixa(Tipo.CAIXA_25,List.of(produto),Set.of(7L),Set.of());
        assertEquals(BigDecimal.valueOf(2),itens.get(0).getQuantidade());
        produto.setCategoria(BEBIDAS);
        assertThrows(IllegalArgumentException.class,()->PersonalizacaoService.validarComposicao(itens));
    }

    @Test void caixaCobraOrcamentoSemAcumularVipOuCupomNoPrecoFechado() {
        var produto=produto(1,"Empada","13.00","2.00",null);
        var caixa=new PersonalizacaoService().sortearCaixa(Tipo.CAIXA_25,List.of(produto),Set.of(),Set.of());
        var cupom=new Cupom("TESTE20",new BigDecimal("20"),BigDecimal.ZERO);
        var calc=new CalculadoraDescontos();
        var apenasCaixa=calc.calcular(new ContextoDesconto(caixa,4,cupom,PlanoAssinatura.DESCONTO_VIP,new BigDecimal("5.00")));
        assertEquals(new BigDecimal("25.00"),apenasCaixa.total());assertTrue(apenasCaixa.freteGratisVip());assertNull(apenasCaixa.cupom());
        var misto=new ArrayList<>(caixa);
        misto.add(new PedidoItem(null,produto,BigDecimal.ONE,produto.getPrecoVenda()));
        var vip=calc.calcular(new ContextoDesconto(misto,4,null,PlanoAssinatura.DESCONTO_VIP,new BigDecimal("5.00")));
        assertEquals(new BigDecimal("36.05"),vip.total());assertEquals("ASSINANTE",vip.estrategia());
        var melhor=calc.calcular(new ContextoDesconto(misto,4,cupom,PlanoAssinatura.DESCONTO_VIP,new BigDecimal("5.00")));
        assertEquals(new BigDecimal("35.40"),melhor.total());assertEquals("CUPOM",melhor.estrategia());assertTrue(melhor.freteGratisVip());
    }

    private static Produto produto(long id,String nome,String preco,String custo,Categoria categoria){
        return new Produto(id,categoria,nome,null,new BigDecimal(preco),new BigDecimal(custo),BigDecimal.ZERO,BigDecimal.ZERO,null);
    }
}
