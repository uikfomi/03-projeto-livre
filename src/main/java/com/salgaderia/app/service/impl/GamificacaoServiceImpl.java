package com.salgaderia.app.service.impl;

import com.salgaderia.app.dao.GamificacaoDAO;
import com.salgaderia.app.dao.UsuarioDAO;
import com.salgaderia.app.dao.impl.*;
import com.salgaderia.app.model.PremioRoleta;
import com.salgaderia.app.model.UsuarioCliente;
import com.salgaderia.app.service.GamificacaoService;
import com.salgaderia.app.util.ConnectionFactory;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.random.RandomGenerator;

/** Sorteia uma única vez por pedido; cliente só pode consultar/sortear seus próprios prêmios. */
public final class GamificacaoServiceImpl implements GamificacaoService {
    private final GamificacaoDAO dados;
    private final UsuarioDAO usuarios;
    private final RandomGenerator aleatorio;

    public GamificacaoServiceImpl(){this(new GamificacaoDAOImpl(),new UsuarioDAOImpl(new ContatoDAOImpl()),new SecureRandom());}
    public GamificacaoServiceImpl(GamificacaoDAO dados,UsuarioDAO usuarios,RandomGenerator aleatorio){
        this.dados=Objects.requireNonNull(dados);this.usuarios=Objects.requireNonNull(usuarios);this.aleatorio=Objects.requireNonNull(aleatorio);
    }

    @Override public Perfil perfil(UsuarioCliente cliente) {
        return ConnectionFactory.executarEmTransacao(c->{
            long id=validarCliente(c,cliente);
            return new Perfil(dados.conquistas(c,id),
                    dados.premiosValidos(c,id).stream().mapToLong(p->p.tipo().pontos()).sum(),
                    dados.cuponsValidos(c,id));
        });
    }

    @Override public PremioRoleta girar(UsuarioCliente cliente,long pedidoId) {
        return ConnectionFactory.executarEmTransacao(c->{
            long id=validarCliente(c,cliente);
            if(!dados.roletaDisponivel(c,pedidoId,id))throw new IllegalArgumentException("A roleta não está disponível para este pedido.");
            var existente=dados.premio(c,pedidoId);
            if(existente.isPresent())return existente.get();
            var tipos=PremioRoleta.Tipo.values();
            var tipo=tipos[aleatorio.nextInt(tipos.length)];
            String codigo=tipo.temCupom()?"FOME-"+UUID.randomUUID().toString().replace("-","").toUpperCase(Locale.ROOT):null;
            PremioRoleta premio=new PremioRoleta(pedidoId,tipo,codigo,true);
            dados.registrarPremio(c,id,premio);return premio;
        });
    }

    private long validarCliente(Connection c,UsuarioCliente cliente)throws SQLException {
        if(cliente==null||cliente.getId()==null||cliente.getCliente().getId()==null||!cliente.isAtivo()||
                !usuarios.bloquearClienteAtivo(c,cliente.getId(),cliente.getCliente().getId()))
            throw new IllegalArgumentException("Cliente ativo obrigatório.");
        return cliente.getId();
    }
}
