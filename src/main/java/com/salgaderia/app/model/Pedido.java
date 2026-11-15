package com.salgaderia.app.model;

import com.salgaderia.app.model.enums.StatusPedido;
import com.salgaderia.app.model.enums.TipoEntrega;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Raiz do agregado: alterações dos itens e do status passam pelo pedido. */
public class Pedido {
    private Long id;
    private final Usuario usuario;
    private final Cliente cliente;
    private StatusPedido status;
    private final TipoEntrega tipoEntrega;
    private final String enderecoEntrega;
    private final LocalDateTime criadoEm;
    private final List<PedidoItem> itens;
    private BigDecimal desconto = BigDecimal.ZERO;

    public Pedido(Long id, Usuario usuario, Cliente cliente, StatusPedido status, TipoEntrega tipoEntrega,
                  String enderecoEntrega, LocalDateTime criadoEm, List<PedidoItem> itens) {
        this.id = Validacao.id(id);
        this.usuario = usuario;
        this.cliente = cliente;
        this.status = Validacao.obrigatorio(status, "status");
        this.tipoEntrega = Validacao.obrigatorio(tipoEntrega, "tipo de entrega");
        this.enderecoEntrega = tipoEntrega == TipoEntrega.ENTREGA
                ? Validacao.texto(enderecoEntrega, "endereço de entrega") : enderecoEntrega;
        this.criadoEm = Validacao.obrigatorio(criadoEm, "data de criação");
        this.itens = new ArrayList<>(Validacao.obrigatorio(itens, "itens"));
        if (this.itens.stream().anyMatch(java.util.Objects::isNull)) throw new IllegalArgumentException("item não pode ser nulo");
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = Validacao.id(id); }
    public Usuario getUsuario() { return usuario; }
    public Cliente getCliente() { return cliente; }
    public StatusPedido getStatus() { return status; }
    public TipoEntrega getTipoEntrega() { return tipoEntrega; }
    public String getEnderecoEntrega() { return enderecoEntrega; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public List<PedidoItem> getItens() { return List.copyOf(itens); }
    public void adicionarItem(PedidoItem item) { exigirPendente(); itens.add(Validacao.obrigatorio(item, "item")); }
    public void removerItem(PedidoItem item) { exigirPendente(); itens.remove(Validacao.obrigatorio(item, "item")); }
    public BigDecimal calcularSubtotal() {
        return itens.stream().map(PedidoItem::calcularSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    public BigDecimal getDesconto() { return desconto; }
    public void setDesconto(BigDecimal desconto) {
        if (desconto == null || desconto.signum() < 0 || desconto.compareTo(calcularSubtotal()) > 0)
            throw new IllegalArgumentException("Desconto inválido");
        this.desconto = desconto;
    }
    public BigDecimal calcularTotal() { return calcularSubtotal().subtract(desconto); }
    public void transicionarPara(StatusPedido destino) {
        if (!status.podeTransicionarPara(destino)) throw new IllegalStateException("Transição de pedido inválida: " + status + " -> " + destino);
        status = destino;
    }
    private void exigirPendente() {
        if (status != StatusPedido.PENDENTE) throw new IllegalStateException("Itens só podem ser alterados em pedido pendente");
    }
}
