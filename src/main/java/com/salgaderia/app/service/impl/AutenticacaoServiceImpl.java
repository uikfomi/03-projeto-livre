package com.salgaderia.app.service.impl;
import com.salgaderia.app.dao.*;
import com.salgaderia.app.dao.impl.*;
import com.salgaderia.app.exception.AutenticacaoInvalidaException;
import com.salgaderia.app.model.*;
import com.salgaderia.app.service.AutenticacaoService;
import com.salgaderia.app.util.*;
import java.util.Objects;
import java.util.Locale;
import java.util.regex.Pattern;
import java.sql.SQLException;
public class AutenticacaoServiceImpl implements AutenticacaoService {
    private final ContatoDAO contatos; private final UsuarioDAO usuarios;
    public AutenticacaoServiceImpl(){this(new ContatoDAOImpl());}
    private AutenticacaoServiceImpl(ContatoDAO contatos){this(contatos,new UsuarioDAOImpl(contatos));}
    public AutenticacaoServiceImpl(ContatoDAO contatos,UsuarioDAO usuarios){this.contatos=Objects.requireNonNull(contatos);this.usuarios=Objects.requireNonNull(usuarios);}
    @Override public Usuario login(String login,String senha){
        if(login==null||senha==null)throw new AutenticacaoInvalidaException("Login ou senha inválidos");
        return ConnectionFactory.executarEmTransacao(c->{
            String informado=login.strip();
            Usuario u=usuarios.buscarPorLogin(c,informado.contains("@")?informado.toLowerCase(Locale.ROOT):informado).orElseThrow(()->new AutenticacaoInvalidaException("Login ou senha inválidos"));
            if(!u.isAtivo()||!Senhas.verificar(senha,u.getSenha()))throw new AutenticacaoInvalidaException("Login ou senha inválidos");
            if(Senhas.legado(u.getSenha())){String hash=Senhas.gerar(senha);usuarios.atualizarSenha(c,u.getId(),hash);u.setSenha(hash);}return u;
        });
    }
    private static final Pattern EMAIL = Pattern.compile(
            "[a-z0-9_+%'-]+(?:\\.[a-z0-9_+%'-]+)*@(?:[a-z0-9](?:[a-z0-9-]{0,61}[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]{0,61}[a-z0-9])?");

    @Override public UsuarioCliente cadastrar(String email,String senha){
        String login=normalizarEmail(email);
        String nome=prefixo(login);
        Cliente cliente=new Cliente(null,nome,null,null,null,null,null);
        UsuarioCliente usuario=new UsuarioCliente(null,nome,login,Senhas.gerar(senha),true,cliente,login);
        long[] ids=ConnectionFactory.executarEmTransacao(c->{
            long contatoId=contatos.inserir(c,cliente);
            try{return new long[]{contatoId,usuarios.inserir(c,usuario,contatoId)};}
            catch(SQLException e){
                if(e.getErrorCode()==1062)throw new IllegalArgumentException("Esse e-mail já está em uso.",e);
                throw e;
            }
        });
        cliente.setId(ids[0]);usuario.setId(ids[1]);return usuario;
    }

    private static String normalizarEmail(String email){
        String valor=email==null?"":email.strip().toLowerCase(Locale.ROOT);
        if(valor.length()>254||valor.indexOf('@')>64||!EMAIL.matcher(valor).matches())
            throw new IllegalArgumentException("Informe um e-mail válido.");
        return valor;
    }
    private static String prefixo(String email){return email.substring(0,email.indexOf('@'));}

    @Override public UsuarioCliente atualizarPerfil(UsuarioCliente usuario,String apelido,String whatsapp,String endereco){
        if(usuario==null||usuario.getId()==null||usuario.getCliente().getId()==null||!usuario.isAtivo())
            throw new IllegalArgumentException("Cliente ativo obrigatório.");
        String nome=opcional(apelido,150,"apelido"), tel=opcional(whatsapp,30,"WhatsApp"), rua=opcional(endereco,255,"endereço");
        return ConnectionFactory.executarEmTransacao(c->{
            long id=usuario.getId(), contato=usuario.getCliente().getId();
            if(!usuarios.bloquearClienteAtivo(c,id,contato))throw new IllegalArgumentException("Cliente ativo obrigatório.");
            UsuarioCliente atual=(UsuarioCliente)usuarios.buscarPorId(c,id).orElseThrow();
            String exibicao=nome!=null?nome:atual.getEmail()!=null?prefixo(atual.getEmail()):atual.getNome();
            usuarios.atualizarApelido(c,id,exibicao);
            contatos.atualizarPerfilCliente(c,contato,exibicao,tel,rua);
            return (UsuarioCliente)usuarios.buscarPorId(c,id).orElseThrow();
        });
    }
    private static String opcional(String texto,int max,String campo){
        if(texto==null||texto.isBlank())return null;
        String limpo=texto.strip();
        if(limpo.codePointCount(0,limpo.length())>max||limpo.codePoints().anyMatch(Character::isISOControl))
            throw new IllegalArgumentException("Confira o "+campo+" (até "+max+" caracteres).");
        return limpo;
    }
}
