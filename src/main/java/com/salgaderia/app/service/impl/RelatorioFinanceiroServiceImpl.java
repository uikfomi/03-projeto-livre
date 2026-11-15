package com.salgaderia.app.service.impl;
import com.salgaderia.app.dao.*;
import com.salgaderia.app.dao.impl.*;
import com.salgaderia.app.service.RelatorioFinanceiroService;
import com.salgaderia.app.util.ConnectionFactory;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
public class RelatorioFinanceiroServiceImpl implements RelatorioFinanceiroService {
    private final PedidoDAO pedidos;private final MovimentacaoEstoqueDAO movimentos;
    public RelatorioFinanceiroServiceImpl(){this(new PedidoDAOImpl(),new MovimentacaoEstoqueDAOImpl());}
    public RelatorioFinanceiroServiceImpl(PedidoDAO pedidos,MovimentacaoEstoqueDAO movimentos){this.pedidos=Objects.requireNonNull(pedidos);this.movimentos=Objects.requireNonNull(movimentos);}
    @Override public java.util.Map<String,BigDecimal> produtosMaisVendidos(LocalDateTime inicio,LocalDateTime fim){validar(inicio,fim);return ConnectionFactory.executarEmTransacao(c->pedidos.produtosMaisVendidos(c,inicio,fim));}
    @Override public java.util.Map<String,BigDecimal> receitaPorCategoria(LocalDateTime inicio,LocalDateTime fim){validar(inicio,fim);return ConnectionFactory.executarEmTransacao(c->pedidos.receitaPorCategoria(c,inicio,fim));}
    private void validar(LocalDateTime inicio,LocalDateTime fim){if(inicio==null||fim==null||!inicio.isBefore(fim))throw new IllegalArgumentException("Intervalo inválido");}
    @Override public Relatorio calcular(LocalDateTime inicio,LocalDateTime fim){
        if(inicio==null||fim==null||!inicio.isBefore(fim))throw new IllegalArgumentException("Intervalo inválido");
        return ConnectionFactory.executarEmTransacao(c->{BigDecimal[] vendas=pedidos.somarVendasEntregues(c,inicio,fim);BigDecimal perdas=movimentos.calcularPerdas(c,inicio,fim);return new Relatorio(vendas[0],vendas[1],perdas,vendas[0].subtract(vendas[1]).subtract(perdas));});
    }
}
