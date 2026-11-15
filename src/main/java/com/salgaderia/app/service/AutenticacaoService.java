package com.salgaderia.app.service;
import com.salgaderia.app.model.*;
public interface AutenticacaoService {
    Usuario login(String login,String senha);
    UsuarioCliente cadastrar(String nome,String login,String senha,Cliente cliente);
}
