package com.salgaderia.app.service.impl;
import com.salgaderia.app.dao.*;
import com.salgaderia.app.dao.impl.*;
import com.salgaderia.app.exception.AvaliacaoNaoPermitidaException;
import com.salgaderia.app.model.*;
import com.salgaderia.app.model.enums.PerfilUsuario;
import com.salgaderia.app.service.AvaliacaoService;
import com.salgaderia.app.util.ConnectionFactory;
import java.sql.*;
import java.util.Objects;
public class AvaliacaoServiceImpl implements AvaliacaoService {
    private final PedidoDAO pedidos;private final AvaliacaoDAO avaliacoes;private final UsuarioDAO usuarios;
    public AvaliacaoServiceImpl(){this(new PedidoDAOImpl(),new AvaliacaoDAOImpl(),new UsuarioDAOImpl(new ContatoDAOImpl()));}
    public AvaliacaoServiceImpl(PedidoDAO pedidos,AvaliacaoDAO avaliacoes,UsuarioDAO usuarios){this.pedidos=Objects.requireNonNull(pedidos);this.avaliacoes=Objects.requireNonNull(avaliacoes);this.usuarios=Objects.requireNonNull(usuarios);}
    @Override public java.util.Map<Long,java.math.BigDecimal> mediasPorProduto(){return ConnectionFactory.executarEmTransacao(avaliacoes::mediasPorProduto);}
    @Override public long salvar(Avaliacao a){
        Objects.requireNonNull(a);if(a.getId()!=null||a.getUsuario().getId()==null||a.getProduto().getId()==null||a.getRespostaAdmin()!=null)throw new IllegalArgumentException("Avaliação inválida");
        long id=ConnectionFactory.executarEmTransacao(c->{Usuario u=usuarios.buscarPorId(c,a.getUsuario().getId()).orElseThrow(()->new AvaliacaoNaoPermitidaException("Usuário inexistente"));
            if(!u.isAtivo()||u.getPerfil()!=PerfilUsuario.CLIENTE||!pedidos.possuiPedidoEntregue(c,u.getId(),a.getProduto().getId()))throw new AvaliacaoNaoPermitidaException("O cliente precisa ter recebido um pedido com o produto");
            return avaliacoes.inserir(c,a);
        });a.setId(id);return id;
    }
    @Override public boolean responder(Usuario admin,long id,String resposta){
        if(resposta==null||resposta.isBlank())throw new IllegalArgumentException("Resposta obrigatória");
        return ConnectionFactory.executarEmTransacao(c->{validarAdmin(c,admin);return avaliacoes.responder(c,id,resposta);});
    }
    @Override public boolean excluir(Usuario admin,long id){return ConnectionFactory.executarEmTransacao(c->{validarAdmin(c,admin);return avaliacoes.excluir(c,id);});}
    private void validarAdmin(Connection c,Usuario admin) throws SQLException {
        if(admin==null||admin.getId()==null)throw new SecurityException("Administrador obrigatório");
        Usuario persistido=usuarios.buscarPorId(c,admin.getId()).orElseThrow(()->new SecurityException("Administrador inexistente"));
        if(!persistido.isAtivo()||persistido.getPerfil()!=PerfilUsuario.ADMIN)throw new SecurityException("Acesso restrito ao administrador");
    }
}
