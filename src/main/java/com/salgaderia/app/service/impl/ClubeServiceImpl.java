package com.salgaderia.app.service.impl;

import com.salgaderia.app.dao.ClubeDAO;
import com.salgaderia.app.dao.UsuarioDAO;
import com.salgaderia.app.dao.impl.*;
import com.salgaderia.app.model.*;
import com.salgaderia.app.service.ClubeService;
import com.salgaderia.app.util.ConnectionFactory;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Objects;

/** Coordena a vigência e a confirmação manual, sem realizar cobrança bancária. */
public final class ClubeServiceImpl implements ClubeService {
    public static final ZoneId FUSO=ZoneId.of("America/Fortaleza");
    private final ClubeDAO clube;
    private final UsuarioDAO usuarios;
    private final Clock relogio;

    public ClubeServiceImpl() {
        this(new ClubeDAOImpl(),new UsuarioDAOImpl(new ContatoDAOImpl()),Clock.system(FUSO));
    }

    public ClubeServiceImpl(ClubeDAO clube,UsuarioDAO usuarios,Clock relogio) {
        this.clube=Objects.requireNonNull(clube);this.usuarios=Objects.requireNonNull(usuarios);this.relogio=Objects.requireNonNull(relogio);
    }

    @Override public Vitrine vitrine(UsuarioCliente cliente) {
        return ConnectionFactory.executarEmTransacao(c->{
            validarCliente(c,cliente,false);
            long id=cliente.getId();
            return new Vitrine(clube.listarPlanos(c,true),clube.assinatura(c,id,false).orElse(null),
                    clube.pendencias(c,id,false).stream().findFirst().orElse(null),LocalDate.now(relogio));
        });
    }

    @Override public Painel painel(UsuarioAdmin admin) {
        return ConnectionFactory.executarEmTransacao(c->{
            validarAdmin(c,admin);
            return new Painel(clube.listarPlanos(c,true),clube.assinantesAtivos(c,LocalDate.now(relogio)),clube.pendencias(c,null,false));
        });
    }

    @Override public void solicitar(UsuarioCliente cliente,long planoId) {
        ConnectionFactory.executarEmTransacao(c->{
            validarCliente(c,cliente,true);
            long id=cliente.getId();
            if(!clube.pendencias(c,id,false).isEmpty())throw new IllegalArgumentException("Já há uma mensalidade aguardando confirmação.");
            PlanoAssinatura plano=clube.planoAtivo(c,planoId).orElseThrow(()->new IllegalArgumentException("Plano indisponível."));
            AssinaturaCliente atual=clube.assinatura(c,id,false).orElse(null);
            LocalDate hoje=LocalDate.now(relogio);
            if(atual!=null&&atual.ativa(hoje)){
                if(atual.planoId()!=planoId)throw new IllegalArgumentException("Finalize a vigência ou cancele antes de mudar de plano.");
                if(!atual.renovavel(hoje))throw new IllegalArgumentException("A renovação abre nos sete dias anteriores ao vencimento.");
            }else clube.prepararAssinatura(c,id,planoId);
            clube.abrirMensalidade(c,id,plano);
            return null;
        });
    }

    @Override public void cancelar(UsuarioCliente cliente) {
        ConnectionFactory.executarEmTransacao(c->{validarCliente(c,cliente,true);clube.cancelar(c,cliente.getId());return null;});
    }

    @Override public void confirmar(UsuarioAdmin admin,long usuarioId,long mensalidadeId) {
        ConnectionFactory.executarEmTransacao(c->{
            validarAdmin(c,admin);
            Usuario atual=usuarios.buscarPorId(c,usuarioId).orElse(null);
            if(!(atual instanceof UsuarioCliente cliente))throw new IllegalArgumentException("Cliente não encontrado.");
            validarCliente(c,cliente,true);
            AssinaturaCliente assinatura=clube.assinatura(c,usuarioId,true).filter(a->!a.cancelada())
                    .orElseThrow(()->new IllegalArgumentException("Assinatura cancelada ou não encontrada."));
            MensalidadeClube mensalidade=clube.pendencias(c,usuarioId,true).stream().filter(m->m.id()==mensalidadeId)
                    .findFirst().orElseThrow(()->new IllegalArgumentException("A mensalidade não está mais pendente."));
            LocalDate hoje=LocalDate.now(relogio);
            LocalDate base=assinatura.ativa(hoje)?assinatura.validaAte():hoje;
            // Mensalidades da migração mantêm a referência e o valor emitidos. A vigência é VIP.
            PlanoAssinatura vip=clube.planoAtivo(c,assinatura.planoId()).filter(p->p.nome().equals(PlanoAssinatura.NOME_VIP))
                    .orElseThrow(()->new IllegalArgumentException("Plano VIP indisponível."));
            clube.confirmarMensalidade(c,mensalidade,base.plusMonths(1),admin.getId(),vip);return null;
        });
    }

    private void validarCliente(Connection c,UsuarioCliente cliente,boolean bloquear)throws SQLException {
        if(cliente==null||cliente.getId()==null||cliente.getCliente().getId()==null||!cliente.isAtivo())
            throw new IllegalArgumentException("Cliente ativo obrigatório.");
        if(bloquear){
            if(!usuarios.bloquearClienteAtivo(c,cliente.getId(),cliente.getCliente().getId()))
                throw new IllegalArgumentException("Cliente sem cadastro ativo.");
        }else if(!(usuarios.buscarPorId(c,cliente.getId()).orElse(null) instanceof UsuarioCliente atual)
                ||!atual.isAtivo()||!Objects.equals(atual.getCliente().getId(),cliente.getCliente().getId()))
            throw new IllegalArgumentException("Cliente sem cadastro ativo.");
    }

    private void validarAdmin(Connection c,UsuarioAdmin admin)throws SQLException {
        if(admin==null||admin.getId()==null||!admin.isAtivo()||
                !(usuarios.buscarPorId(c,admin.getId()).orElse(null) instanceof UsuarioAdmin atual)||!atual.isAtivo())
            throw new IllegalArgumentException("Administrador ativo obrigatório.");
    }
}
