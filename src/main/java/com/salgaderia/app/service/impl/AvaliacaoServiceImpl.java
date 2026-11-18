package com.salgaderia.app.service.impl;
import com.salgaderia.app.dao.*;
import com.salgaderia.app.dao.impl.*;
import com.salgaderia.app.exception.AvaliacaoNaoPermitidaException;
import com.salgaderia.app.model.*;
import com.salgaderia.app.model.enums.PerfilUsuario;
import com.salgaderia.app.service.AvaliacaoService;
import com.salgaderia.app.service.FotoAvaliacaoStorage;
import com.salgaderia.app.util.ConnectionFactory;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.sql.*;
import java.util.Objects;
public class AvaliacaoServiceImpl implements AvaliacaoService {
    private final PedidoDAO pedidos;private final AvaliacaoDAO avaliacoes;private final UsuarioDAO usuarios;
    public AvaliacaoServiceImpl(){this(new PedidoDAOImpl(),new AvaliacaoDAOImpl(),new UsuarioDAOImpl(new ContatoDAOImpl()));}
    private final FotoAvaliacaoStorage fotos;
    public AvaliacaoServiceImpl(PedidoDAO pedidos,AvaliacaoDAO avaliacoes,UsuarioDAO usuarios){this(pedidos,avaliacoes,usuarios,new FotoAvaliacaoStorage());}
    public AvaliacaoServiceImpl(PedidoDAO pedidos,AvaliacaoDAO avaliacoes,UsuarioDAO usuarios,FotoAvaliacaoStorage fotos){this.pedidos=Objects.requireNonNull(pedidos);this.avaliacoes=Objects.requireNonNull(avaliacoes);this.usuarios=Objects.requireNonNull(usuarios);this.fotos=Objects.requireNonNull(fotos);}
    @Override public java.util.Map<Long,java.math.BigDecimal> mediasPorProduto(){return ConnectionFactory.executarEmTransacao(avaliacoes::mediasPorProduto);}
    @Override public java.util.List<AvaliacaoResumo> listar(){return ConnectionFactory.executarEmTransacao(avaliacoes::listar);}
    @Override public java.util.List<AvaliacaoFoto> fotosRecentes(){return ConnectionFactory.executarEmTransacao(avaliacoes::fotosRecentes);}
    @Override public long salvar(Avaliacao a,Path foto){
        Objects.requireNonNull(a);if(a.getId()!=null||a.getUsuario().getId()==null||a.getProduto().getId()==null||a.getRespostaAdmin()!=null||a.getFotoUrl()!=null)throw new IllegalArgumentException("Avaliação inválida");
        long id;
        try {
            id=ConnectionFactory.executarEmTransacao(c->{
                Usuario u=usuarios.buscarPorId(c,a.getUsuario().getId()).orElseThrow(()->new AvaliacaoNaoPermitidaException("Usuário inexistente"));
                if(!u.isAtivo()||u.getPerfil()!=PerfilUsuario.CLIENTE||!pedidos.possuiPedidoEntregue(c,u.getId(),a.getProduto().getId()))throw new AvaliacaoNaoPermitidaException("O cliente precisa ter recebido um pedido com o produto");
                if(foto!=null){
                    try{a.setFotoUrl(fotos.salvar(foto));}
                    catch(IOException e){throw new UncheckedIOException("Não foi possível armazenar a foto.",e);}
                }
                return avaliacoes.inserir(c,a);
            });
        } catch (RuntimeException | Error falha) {
            limparFoto(a.getFotoUrl());
            a.setFotoUrl(null);
            throw falha;
        }
        a.setId(id);return id;
    }
    @Override public boolean responder(Usuario admin,long id,String resposta){
        if(resposta==null||resposta.isBlank())throw new IllegalArgumentException("Resposta obrigatória");
        return ConnectionFactory.executarEmTransacao(c->{validarAdmin(c,admin);return avaliacoes.responder(c,id,resposta);});
    }
    @Override public boolean excluir(Usuario admin,long id){
        record Remocao(boolean excluiu,String foto) { }
        Remocao remocao=ConnectionFactory.executarEmTransacao(c->{
            validarAdmin(c,admin);String foto=avaliacoes.bloquearFoto(c,id);return new Remocao(avaliacoes.excluir(c,id),foto);
        });
        if(remocao.excluiu())limparFoto(remocao.foto());return remocao.excluiu();
    }
    private void limparFoto(String foto){
        try{fotos.excluir(foto);}
        catch(IOException e){System.getLogger(getClass().getName()).log(System.Logger.Level.WARNING,"Falha ao remover foto de avaliação.",e);}
    }
    private void validarAdmin(Connection c,Usuario admin) throws SQLException {
        if(admin==null||admin.getId()==null)throw new SecurityException("Administrador obrigatório");
        Usuario persistido=usuarios.buscarPorId(c,admin.getId()).orElseThrow(()->new SecurityException("Administrador inexistente"));
        if(!persistido.isAtivo()||persistido.getPerfil()!=PerfilUsuario.ADMIN)throw new SecurityException("Acesso restrito ao administrador");
    }
}
