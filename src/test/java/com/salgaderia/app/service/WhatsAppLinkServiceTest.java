package com.salgaderia.app.service;

import com.salgaderia.app.model.*;
import com.salgaderia.app.model.enums.*;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class WhatsAppLinkServiceTest {
    @Test void builderCodificaTextoEValidaTelefone() {
        var uri=new WhatsAppLinkService.Builder("(85) 99999-9999").linha("Olá & bom dia!").linha("Pronto?").construir();
        assertEquals("wa.me",uri.getHost());
        assertEquals("/5585999999999",uri.getPath());
        assertEquals("text=Olá & bom dia!\nPronto?",URLDecoder.decode(uri.getRawQuery(),StandardCharsets.UTF_8));
        assertThrows(IllegalArgumentException.class,()->new WhatsAppLinkService.Builder("123").construir());
    }
    @Test void mensagemDoPedidoUsaValorRealEDadosDaEntrega() {
        Produto produto=new Produto(1L,null,"Coxinha & queijo",null,new BigDecimal("10"),BigDecimal.ONE,BigDecimal.TEN,BigDecimal.ONE,null);
        Pedido pedido=new Pedido(5L,null,null,StatusPedido.PENDENTE,TipoEntrega.ENTREGA,"Rua São José",LocalDateTime.now(),List.of(new PedidoItem(null,produto,new BigDecimal("2"),BigDecimal.TEN)));
        pedido.setDesconto(new BigDecimal("2"));
        var link=new WhatsAppLinkService().acompanhar(pedido);
        String msg=URLDecoder.decode(link.getRawQuery(),StandardCharsets.UTF_8);
        assertEquals("/5585999999999",link.getPath());assertTrue(msg.contains("#5"));assertTrue(msg.contains("2 x Coxinha & queijo"));
        assertTrue(msg.contains("Rua São José"));assertTrue(msg.contains("Pagamento: não informado"));assertTrue(msg.contains("18,00"));
    }
    @Test void statusUsaTelefoneDoClienteEStatusInformado() {
        String query=URLDecoder.decode(new WhatsAppLinkService().status("5585988887777",42,StatusPedido.CANCELADO).getRawQuery(),StandardCharsets.UTF_8);
        assertTrue(query.contains("#42"));assertTrue(query.contains("CANCELADO"));
    }
}
