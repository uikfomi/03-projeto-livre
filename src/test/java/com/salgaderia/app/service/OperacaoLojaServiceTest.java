package com.salgaderia.app.service;

import com.salgaderia.app.model.ConfiguracaoLoja;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OperacaoLojaServiceTest {
    @Test void aberturaInclusivaEFechamentoExclusivoEmAmbasAsFaixas() {
        var dia=new ConfiguracaoLoja(true,LocalTime.of(8,0),LocalTime.of(23,0),true);
        assertFalse(OperacaoLojaService.estaAberta(dia,LocalTime.of(7,59)));
        assertTrue(OperacaoLojaService.estaAberta(dia,LocalTime.of(8,0)));
        assertFalse(OperacaoLojaService.estaAberta(dia,LocalTime.of(23,0)));
        var noite=new ConfiguracaoLoja(true,LocalTime.of(18,0),LocalTime.of(1,0),true);
        assertFalse(OperacaoLojaService.estaAberta(noite,LocalTime.of(17,59)));
        assertTrue(OperacaoLojaService.estaAberta(noite,LocalTime.of(18,0)));
        assertTrue(OperacaoLojaService.estaAberta(noite,LocalTime.MIDNIGHT));
        assertTrue(OperacaoLojaService.estaAberta(noite,LocalTime.of(0,59,59)));
        assertFalse(OperacaoLojaService.estaAberta(noite,LocalTime.of(1,0)));
        assertThrows(IllegalArgumentException.class,()->new ConfiguracaoLoja(true,LocalTime.NOON,LocalTime.NOON,true));
    }

    @Test void manualEAutomaticoSeCombinamEEncomendasRespeitamExpediente() {
        var noite=new ConfiguracaoLoja(true,LocalTime.of(18,0),LocalTime.of(1,0),true);
        var manual=new ConfiguracaoLoja(false,noite.abertura(),noite.fechamento(),false);
        var aberto=new ConfiguracaoLoja(true,noite.abertura(),noite.fechamento(),false);
        var service=VendaTeste.operacao(noite);
        var hoje=VendaTeste.AGORA.toLocalDate();
        assertEquals("🔴 Loja Fechada no momento — Abre às 18:00",service.rotulo(noite));
        assertTrue(service.rotulo(manual).contains("fechamento manual"));
        assertThrows(IllegalArgumentException.class,()->service.validarPedido(noite,null));
        assertThrows(IllegalArgumentException.class,()->service.validarPedido(manual,null));
        assertDoesNotThrow(()->service.validarPedido(aberto,null));
        assertThrows(IllegalArgumentException.class,()->service.validarPedido(manual,hoje.atTime(0,59)));
        assertThrows(IllegalArgumentException.class,()->service.validarPedido(aberto,hoje.plusDays(1).atTime(1,0)));
        assertDoesNotThrow(()->service.validarPedido(manual,hoje.atTime(18,0)));
        assertDoesNotThrow(()->service.validarPedido(manual,hoje.plusDays(1).atTime(0,59)));
    }
}
