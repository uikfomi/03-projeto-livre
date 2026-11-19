package com.salgaderia.app.dao;

import com.salgaderia.app.model.enums.StatusPagamento;
import com.salgaderia.app.model.enums.StatusPedido;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PixPagamentoDAO {
    enum Provedor { MERCADO_PAGO, DEMO }
    record Dados(long id,long usuarioId,long contatoId,StatusPedido pedido,StatusPagamento pagamento,Provedor provedor,
                 String idRemoto,String referencia,String email,String documento,LocalDateTime tentativa,boolean podeRepetirCriacao,
                 BigDecimal valor,String payload) { }

    void preparar(Connection c,long pedidoId,Provedor provedor,String referencia,String email,String documento,StatusPagamento status) throws SQLException;
    Optional<Dados> buscar(Connection c,long pedidoId,boolean bloquear) throws SQLException;
    List<Dados> pendentesConhecidos(Connection c,long cursor) throws SQLException;
    void marcarTentativa(Connection c,long pedidoId) throws SQLException;
    void vincular(Connection c,long pedidoId,String transacao,String payload) throws SQLException;
    void pagar(Connection c,long pedidoId) throws SQLException;
}
