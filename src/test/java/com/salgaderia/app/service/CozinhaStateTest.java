package com.salgaderia.app.service;

import com.salgaderia.app.model.enums.*;
import com.salgaderia.app.service.cozinha.EtapaCozinha;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CozinhaStateTest {
    @Test void estadosAcompanhamStatusEProgresso(){
        assertEquals("Massa na bancada",EtapaCozinha.de(StatusPedido.PENDENTE).mensagem(TipoEntrega.LOCAL));
        assertTrue(EtapaCozinha.de(StatusPedido.PREPARANDO).mensagem(TipoEntrega.ENTREGA).contains("Fritando"));
        assertEquals("Embalando quentinho",EtapaCozinha.de(StatusPedido.PRONTO).mensagem(TipoEntrega.RETIRADA));
        assertEquals("Saiu para entrega",EtapaCozinha.de(StatusPedido.SAIU_ENTREGA).mensagem(TipoEntrega.ENTREGA));
        assertEquals("Pedido concluído",EtapaCozinha.de(StatusPedido.ENTREGUE).mensagem(TipoEntrega.ENTREGA));
        assertFalse(EtapaCozinha.de(StatusPedido.CANCELADO).aguardaCozinha());
        assertTrue(EtapaCozinha.de(StatusPedido.PREPARANDO).progresso()>EtapaCozinha.de(StatusPedido.PENDENTE).progresso());
    }
}
