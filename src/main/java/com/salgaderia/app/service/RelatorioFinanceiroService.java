package com.salgaderia.app.service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
public interface RelatorioFinanceiroService {
    /** Intervalo semiaberto: início inclusivo, fim exclusivo; vendas filtradas pela criação do pedido entregue. */
    Relatorio calcular(LocalDateTime inicio,LocalDateTime fim);
    record Relatorio(BigDecimal faturamentoBruto,BigDecimal custoTotalProducao,BigDecimal perdas,BigDecimal lucroLiquidoReal) { }
}
