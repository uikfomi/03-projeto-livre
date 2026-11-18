package com.salgaderia.app.service;

import com.salgaderia.app.dao.GamificacaoDAO;
import com.salgaderia.app.model.FichaVitrine;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

public final class ConquistasObserver implements PedidoConcluidoObserver {
    private final GamificacaoDAO dados;
    public ConquistasObserver(GamificacaoDAO dados) { this.dados=Objects.requireNonNull(dados); }

    @Override public void aoAlterarPedido(Connection c,Evento evento)throws SQLException {
        if(evento.etapa()==Etapa.CANCELADO){dados.revogarCupomDoPremio(c,evento.pedidoId());return;}
        var cliente=evento.cliente();
        if(cliente==null)return;
        dados.desbloquear(c,cliente.usuarioId(),"PRIMEIRO_PEDIDO");
        if(evento.etapa()==Etapa.FINALIZADO&&evento.vitrine())dados.liberarRoleta(c,evento.pedidoId());
        if(evento.etapa()==Etapa.ENTREGUE){
            BigDecimal quantidade=dados.quantidadesPorCategoria(c,evento.pedidoId()).stream()
                    .filter(q->FichaVitrine.categoriaSalgado(q.categoria()))
                    .map(GamificacaoDAO.QuantidadeCategoria::quantidade).reduce(BigDecimal.ZERO,BigDecimal::add);
            if(quantidade.compareTo(BigDecimal.valueOf(100))>=0)dados.desbloquear(c,cliente.usuarioId(),"REI_DO_CENTO");
            if(dados.entregues(c,cliente.contatoId())>=3)dados.desbloquear(c,cliente.usuarioId(),"CLIENTE_FIEL");
        }
    }
}
