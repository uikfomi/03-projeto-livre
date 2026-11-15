package com.salgaderia.app.model;

import com.salgaderia.app.exception.EstoqueInsuficienteException;
import com.salgaderia.app.model.enums.*;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class DominioTest {
    private Produto produto() {
        return new Produto(null, null, "Coxinha", null, new BigDecimal("8.00"), new BigDecimal("3.00"),
                new BigDecimal("2.000"), new BigDecimal("1.000"), null);
    }
    @Test void estoqueLucroEValidacoes() {
        Produto produto = produto();
        assertEquals(new BigDecimal("5.00"), produto.calcularLucroUnitario());
        produto.removerEstoque(BigDecimal.ONE);
        assertTrue(produto.isEstoqueBaixo());
        EstoqueInsuficienteException erro = assertThrows(EstoqueInsuficienteException.class,
                () -> produto.removerEstoque(new BigDecimal("2")));
        assertEquals("Coxinha", erro.getNomeProduto());
        assertEquals(0, erro.getEstoqueDisponivel().compareTo(BigDecimal.ONE));
        assertThrows(IllegalArgumentException.class, () -> produto.adicionarEstoque(BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class, () -> produto.setPrecoVenda(BigDecimal.ONE.negate()));
        produto.adicionarEstoque(BigDecimal.ONE);
        assertEquals(0, produto.getEstoqueAtual().compareTo(new BigDecimal("2")));
    }
    @Test void pedidoProtegeItensETransicoes() {
        PedidoItem item = new PedidoItem(null, produto(), new BigDecimal("2"), new BigDecimal("8.00"));
        List<PedidoItem> origem = new ArrayList<>();
        Pedido pedido = new Pedido(null, null, null, StatusPedido.PENDENTE, TipoEntrega.RETIRADA,
                null, LocalDateTime.now(), origem);
        origem.add(item);
        assertTrue(pedido.getItens().isEmpty());
        pedido.adicionarItem(item);
        assertEquals(new BigDecimal("16.00"), pedido.calcularTotal());
        assertThrows(UnsupportedOperationException.class, () -> pedido.getItens().clear());
        assertThrows(IllegalStateException.class, () -> pedido.transicionarPara(StatusPedido.ENTREGUE));
        pedido.transicionarPara(StatusPedido.PREPARANDO);
        assertThrows(IllegalStateException.class, () -> pedido.removerItem(item));
        assertThrows(IllegalArgumentException.class, () -> new Pedido(null, null, null, StatusPedido.PENDENTE,
                TipoEntrega.ENTREGA, "  ", LocalDateTime.now(), List.of()));
    }
    @Test void polimorfismoEValidacaoDeAvaliacao() {
        Cliente cliente = new Cliente(null, "Ana", null, null, "123", null, null);
        Usuario usuario = new UsuarioCliente(null, "Ana", "ana", "senha", true, cliente);
        Usuario admin = new UsuarioAdmin(null, "Admin", "admin", "senha", true);
        assertFalse(usuario.podeAcessarPainelGerencial());
        assertTrue(admin.podeAcessarPainelGerencial());
        Contato fornecedor = new Fornecedor(null, "João", null, null, "Empresa", "12.345.678/0001-99", CategoriaInsumo.FARINHA, 1);
        assertTrue(cliente.getResumoContato().contains("Cliente"));
        assertTrue(fornecedor.getResumoContato().contains("Fornecedor"));
        Avaliacao avaliacao = new Avaliacao(null, usuario, produto(), 5, null, null, LocalDateTime.now());
        assertThrows(IllegalArgumentException.class, () -> avaliacao.setNota(0));
        assertThrows(IllegalArgumentException.class, () -> avaliacao.setNota(6));
        assertEquals(5, avaliacao.getNota());
    }
}
