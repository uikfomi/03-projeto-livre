package com.salgaderia.app.model.enums;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class EnumsTest {
    @Test
    void verificaTodasAsTransicoes() {
        Map<StatusPedido, Set<StatusPedido>> permitidas = Map.of(
                StatusPedido.PENDENTE, Set.of(StatusPedido.PREPARANDO, StatusPedido.CANCELADO),
                StatusPedido.PREPARANDO, Set.of(StatusPedido.PRONTO, StatusPedido.CANCELADO),
                StatusPedido.PRONTO, Set.of(StatusPedido.ENTREGUE, StatusPedido.CANCELADO),
                StatusPedido.ENTREGUE, Set.of(),
                StatusPedido.CANCELADO, Set.of());
        for (StatusPedido origem : StatusPedido.values()) {
            assertFalse(origem.podeTransicionarPara(null));
            for (StatusPedido destino : StatusPedido.values()) {
                assertEquals(permitidas.get(origem).contains(destino),
                        origem.podeTransicionarPara(destino), origem + " -> " + destino);
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
