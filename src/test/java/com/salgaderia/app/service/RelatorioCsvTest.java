package com.salgaderia.app.service;

import com.salgaderia.app.service.impl.RelatorioFinanceiroServiceImpl;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

class RelatorioCsvTest {
    @TempDir Path diretorio;
    @Test void csvDistingueBrutoDescontoELucro() throws Exception {
        var r=new RelatorioFinanceiroService.Relatorio(new BigDecimal("100"),new BigDecimal("85"),new BigDecimal("30"),new BigDecimal("5"),new BigDecimal("50"));
        Path arquivo=diretorio.resolve("balanco.csv");
        new RelatorioFinanceiroServiceImpl().exportarCsv(r,arquivo);
        String csv=Files.readString(arquivo,StandardCharsets.UTF_8);
        assertTrue(csv.startsWith("\ufeff"));assertTrue(csv.contains("Faturamento Bruto;100,00"));
        assertTrue(csv.contains("Descontos;15,00"));assertTrue(csv.contains("Custos de Produção;30,00"));
        assertTrue(csv.contains("Perdas;5,00"));assertTrue(csv.contains("Lucro Líquido Real;50,00"));
    }
}
