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
import java.time.LocalDateTime;
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
                    dados.pontosDosPedidos(c,id)+dados.premiosValidos(c,id).stream().mapToLong(p->p.tipo().pontos()).sum(),
                    dados.cuponsValidos(c,id));
        });
    }

    @Override public PremioRoleta girar(UsuarioCliente cliente,long pedidoId) {
        return sortear(cliente,pedidoId,false);
    }
    @Override public PremioRoleta girarExtra(UsuarioCliente cliente,long pedidoId) {
        return sortear(cliente,pedidoId,true);
    }
    private PremioRoleta sortear(UsuarioCliente cliente,long pedidoId,boolean extra){
        return ConnectionFactory.executarEmTransacao(c->{
            long id=validarCliente(c,cliente);
            var elegivel=dados.roletaElegivel(c,pedidoId,id).orElseThrow(()->new IllegalArgumentException("A roleta não está disponível antes do pagamento ou da entrega."));
            var existente=dados.premio(c,pedidoId);
            LocalDateTime agora=dados.horarioDoBanco(c);
            if(existente.isPresent()&&!extra)return existente.get();
            boolean vip=elegivel.valor().compareTo(PremioRoleta.LIMITE_VIP)>0;
            LocalDateTime inicio=agora;
            if(extra){
                var anterior=existente.filter(PremioRoleta::giroExtra).orElseThrow(()->new IllegalArgumentException("Não há giro extra disponível."));
                if(!agora.isBefore(anterior.validoAte()))throw new IllegalArgumentException("O giro extra expirou.");
                vip=anterior.vip();inicio=anterior.registradoEm();
            }else dados.ultimoGiro(c,id).map(t->t.plusHours(24)).filter(agora::isBefore).ifPresent(t->{
                throw new IllegalArgumentException("Limite de um pedido por 24 horas. Próximo giro a partir de "+t.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM HH:mm"))+".");
            });
            var tipos=PremioRoleta.fatias(vip).stream().filter(t->!extra||t!=PremioRoleta.Tipo.GIRO_EXTRA).toList();
            var tipo=tipos.get(aleatorio.nextInt(tipos.size()));
            String codigo=tipo.temCupom()&&!elegivel.demo()?"FOME-"+UUID.randomUUID().toString().replace("-","").toUpperCase(Locale.ROOT):null;
            PremioRoleta premio=new PremioRoleta(pedidoId,tipo,codigo,true,vip,tipo==PremioRoleta.Tipo.GIRO_EXTRA,inicio);
            dados.registrarPremio(c,id,premio,extra);return premio;
        });
    }

    private long validarCliente(Connection c,UsuarioCliente cliente)throws SQLException {
        if(cliente==null||cliente.getId()==null||cliente.getCliente().getId()==null||!cliente.isAtivo()||
                !usuarios.bloquearClienteAtivo(c,cliente.getId(),cliente.getCliente().getId()))
            throw new IllegalArgumentException("Cliente ativo obrigatório.");
        return cliente.getId();
    }
}
