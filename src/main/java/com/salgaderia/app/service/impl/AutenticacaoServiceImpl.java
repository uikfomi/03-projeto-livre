package com.salgaderia.app.service.impl;
import com.salgaderia.app.dao.*;
import com.salgaderia.app.dao.impl.*;
import com.salgaderia.app.exception.AutenticacaoInvalidaException;
import com.salgaderia.app.model.*;
import com.salgaderia.app.service.AutenticacaoService;
import com.salgaderia.app.util.*;
import java.util.Objects;
public class AutenticacaoServiceImpl implements AutenticacaoService {
    private final ContatoDAO contatos; private final UsuarioDAO usuarios;
    public AutenticacaoServiceImpl(){this(new ContatoDAOImpl());}
    private AutenticacaoServiceImpl(ContatoDAO contatos){this(contatos,new UsuarioDAOImpl(contatos));}
    public AutenticacaoServiceImpl(ContatoDAO contatos,UsuarioDAO usuarios){this.contatos=Objects.requireNonNull(contatos);this.usuarios=Objects.requireNonNull(usuarios);}
    @Override public Usuario login(String login,String senha){
        if(login==null||senha==null)throw new AutenticacaoInvalidaException("Login ou senha inválidos");
        return ConnectionFactory.executarEmTransacao(c->{
            Usuario u=usuarios.buscarPorLogin(c,login).orElseThrow(()->new AutenticacaoInvalidaException("Login ou senha inválidos"));
            if(!u.isAtivo()||!Senhas.verificar(senha,u.getSenha()))throw new AutenticacaoInvalidaException("Login ou senha inválidos");
            if(Senhas.legado(u.getSenha())){String hash=Senhas.gerar(senha);usuarios.atualizarSenha(c,u.getId(),hash);u.setSenha(hash);}return u;
        });
    }
    @Override public UsuarioCliente cadastrar(String nome,String login,String senha,Cliente cliente){
        Objects.requireNonNull(cliente,"cliente");if(cliente.getId()!=null)throw new IllegalArgumentException("Cliente já cadastrado");
        UsuarioCliente usuario=new UsuarioCliente(null,nome,login,Senhas.gerar(senha),true,cliente);
        long[] ids=ConnectionFactory.executarEmTransacao(c->{long contatoId=contatos.inserir(c,cliente);long usuarioId=usuarios.inserir(c,usuario,contatoId);return new long[]{contatoId,usuarioId};});
        cliente.setId(ids[0]);usuario.setId(ids[1]);return usuario;
    }
}
