package com.salgaderia.app.service.desconto;

import com.salgaderia.app.model.Categoria;
import com.salgaderia.app.model.PedidoItem;
import com.salgaderia.app.model.Produto;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class EstrategiasDescontoTest {
    private PedidoItem item(String categoria, String quantidade, String preco) {
        Categoria cat = categoria == null ? null : new Categoria(1L,categoria,null);
        Produto produto = new Produto(1L,cat,"Produto",null,new BigDecimal(preco),BigDecimal.ONE,new BigDecimal("500"),BigDecimal.ZERO,null);
        return new PedidoItem(null,produto,new BigDecimal(quantidade),new BigDecimal(preco));
    }
    private void valor(String esperado, BigDecimal atual) {
        assertEquals(0,new BigDecimal(esperado).compareTo(atual));
    }
    @Test void cupomPercentualEMinimo() {
        var estrategia=new DescontoCupomPercentual();
        var dez=new Cupom("UIKFOMI10",new BigDecimal("10"),BigDecimal.ZERO);
        var festa=new Cupom("FESTA15",new BigDecimal("15"),new BigDecimal("80"));
        valor("10.00",estrategia.calcular(new ContextoDesconto(List.of(item("Fritos","1","100")),0,dez)));
        valor("0",estrategia.calcular(new ContextoDesconto(List.of(item("Fritos","1","79.99")),0,festa)));
        valor("12.00",estrategia.calcular(new ContextoDesconto(List.of(item("Fritos","1","80")),0,festa)));
        valor("0",estrategia.calcular(new ContextoDesconto(List.of(item("Fritos","1","80")),0,null)));
    }
    @Test void centoContaApenasSalgadosEDescontaApenasItensElegiveis() {
        var estrategia=new DescontoCentoSalgados();
        valor("0",estrategia.calcular(new ContextoDesconto(List.of(item("Fritos","99","2"),item("Bebidas","50","10")),0,null)));
        valor("24.00",estrategia.calcular(new ContextoDesconto(List.of(item("Salgados Fritos","40","2"),item("ASSÁDOS","60","2"),item("Doces assados","200","10"),item("Bebidas","200","5")),0,null)));
        valor("0",estrategia.calcular(new ContextoDesconto(List.of(item(null,"100","2")),0,null)));
    }
    @Test void fidelidadeSomenteAPartirDeTresPedidosEntregues() {
        var estrategia=new DescontoFidelidade();var itens=List.of(item("Bebidas","1","80"));
        valor("0",estrategia.calcular(new ContextoDesconto(itens,2,null)));
        valor("4.00",estrategia.calcular(new ContextoDesconto(itens,3,null)));
    }
    @Test void maiorDescontoUnicoSemAcumular() {
        var itens=List.of(item("Fritos","100","1"),item("Bebidas","1","100"));
        var calculadora=new CalculadoraDescontos();
        var dez=new Cupom("UIKFOMI10",new BigDecimal("10"),BigDecimal.ZERO);
        var resultado=calculadora.calcular(new ContextoDesconto(itens,3,dez));
        valor("200",resultado.subtotal());valor("20",resultado.desconto());valor("180",resultado.total());
        assertEquals("CUPOM",resultado.estrategia());assertEquals("UIKFOMI10",resultado.cupom());
        var cento=calculadora.calcular(new ContextoDesconto(List.of(item("Fritos","100","1")),3,dez));
        valor("12",cento.desconto());assertEquals("CENTO",cento.estrategia());assertNull(cento.cupom());
    }
    @Test void arredondaEmCentavosEMantemZeroSemElegibilidade() {
        var calculadora=new CalculadoraDescontos();
        var resultado=calculadora.calcular(new ContextoDesconto(List.of(item("Bebidas","1","10.05")),3,null));
        valor("0.50",resultado.desconto());valor("9.55",resultado.total());
        assertEquals("NENHUM",calculadora.calcular(new ContextoDesconto(List.of(item(null,"1","10")),0,null)).estrategia());
    }
}
