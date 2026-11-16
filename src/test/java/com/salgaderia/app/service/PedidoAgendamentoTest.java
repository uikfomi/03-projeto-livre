package com.salgaderia.app.service;

import com.salgaderia.app.model.*;
import com.salgaderia.app.model.enums.*;
import com.salgaderia.app.service.impl.PedidoVendaServiceImpl;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PedidoAgendamentoTest {
    @Test void pedidoNovoNaoPodeSerAgendadoNoPassado(){
        Produto produto=new Produto(1L,null,"Coxinha",null,BigDecimal.TEN,BigDecimal.ONE,BigDecimal.TEN,BigDecimal.ONE,null);
        Pedido pedido=new Pedido(null,null,null,StatusPedido.PENDENTE,TipoEntrega.LOCAL,null,LocalDateTime.now(),List.of(new PedidoItem(null,produto,BigDecimal.ONE,BigDecimal.TEN)));
        pedido.setAgendadoPara(LocalDateTime.now().minusMinutes(1));
        assertThrows(IllegalArgumentException.class,()->new PedidoVendaServiceImpl().finalizar(pedido));
        assertNull(pedido.getId());
    }
}
