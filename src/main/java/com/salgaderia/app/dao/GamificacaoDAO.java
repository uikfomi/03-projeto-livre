package com.salgaderia.app.dao;

import com.salgaderia.app.model.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface GamificacaoDAO {
    record Conta(long usuarioId,long contatoId) { }
    record QuantidadeCategoria(Categoria categoria,BigDecimal quantidade) { }
    Optional<Conta> contaPorContato(Connection c,long contatoId) throws SQLException;
    Optional<Conta> contaDoPedido(Connection c,long pedidoId) throws SQLException;
    List<ConquistaCliente> conquistas(Connection c,long usuarioId) throws SQLException;
    void desbloquear(Connection c,long usuarioId,String codigo) throws SQLException;
    int entregues(Connection c,long contatoId) throws SQLException;
    List<QuantidadeCategoria> quantidadesPorCategoria(Connection c,long pedidoId) throws SQLException;
    void liberarRoleta(Connection c,long pedidoId) throws SQLException;
    boolean roletaDisponivel(Connection c,long pedidoId,long usuarioId) throws SQLException;
    Optional<PremioRoleta> premio(Connection c,long pedidoId) throws SQLException;
    List<PremioRoleta> premiosValidos(Connection c,long usuarioId) throws SQLException;
    List<String> cuponsValidos(Connection c,long usuarioId) throws SQLException;
    void registrarPremio(Connection c,long usuarioId,PremioRoleta premio) throws SQLException;
    void revogarCupomDoPremio(Connection c,long pedidoId) throws SQLException;
}
