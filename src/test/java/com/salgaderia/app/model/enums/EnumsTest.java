package com.salgaderia.app.model.enums;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class EnumsTest {
    @Test
    void verificaTodasAsTransicoes() {
        for (TipoEntrega modalidade : TipoEntrega.values()) {
            boolean entrega = modalidade == TipoEntrega.ENTREGA;
            verificar(modalidade, entrega);
        }
    }
    private void verificar(TipoEntrega modalidade, boolean entrega) {
        Map<StatusPedido, Set<StatusPedido>> permitidas = Map.of(
                StatusPedido.PENDENTE, Set.of(StatusPedido.PREPARANDO, StatusPedido.CANCELADO),
                StatusPedido.PREPARANDO, Set.of(StatusPedido.PRONTO, StatusPedido.CANCELADO),
                StatusPedido.PRONTO, Set.of(entrega ? StatusPedido.SAIU_ENTREGA : StatusPedido.ENTREGUE, StatusPedido.CANCELADO),
                StatusPedido.SAIU_ENTREGA, entrega ? Set.of(StatusPedido.ENTREGUE, StatusPedido.CANCELADO) : Set.of(),
                StatusPedido.ENTREGUE, Set.of(),
                StatusPedido.CANCELADO, Set.of());
        for (StatusPedido origem : StatusPedido.values()) {
            assertFalse(origem.podeTransicionarPara(null, modalidade));
            for (StatusPedido destino : StatusPedido.values()) {
                assertEquals(permitidas.get(origem).contains(destino),
                        origem.podeTransicionarPara(destino, modalidade), origem + " -> " + destino);
            }
        }
    }

    @Test
    void somenteDinheiroPermiteTroco() {
        for (FormaPagamento forma : FormaPagamento.values()) {
            assertEquals(forma == FormaPagamento.DINHEIRO, forma.isPermiteTroco(), forma.name());
        }
    }
}
