package com.salgaderia.app.dao;

import com.salgaderia.app.model.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ClubeDAO {
    List<PlanoAssinatura> listarPlanos(Connection c,boolean apenasAtivos) throws SQLException;
    Optional<PlanoAssinatura> planoAtivo(Connection c,long id) throws SQLException;
    void salvarPlano(Connection c,PlanoAssinatura plano) throws SQLException;
    Optional<AssinaturaCliente> assinatura(Connection c,long usuarioId,boolean bloquear) throws SQLException;
    List<AssinaturaCliente> assinantesAtivos(Connection c,LocalDate hoje) throws SQLException;
    List<MensalidadeClube> pendencias(Connection c,Long usuarioId,boolean bloquear) throws SQLException;
    void prepararAssinatura(Connection c,long usuarioId,long planoId) throws SQLException;
    void abrirMensalidade(Connection c,long usuarioId,PlanoAssinatura plano) throws SQLException;
    void confirmarMensalidade(Connection c,MensalidadeClube mensalidade,LocalDate ate,long adminId) throws SQLException;
    void cancelar(Connection c,long usuarioId) throws SQLException;
}
