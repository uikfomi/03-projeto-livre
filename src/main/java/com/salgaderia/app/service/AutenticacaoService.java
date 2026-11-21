package com.salgaderia.app.service;
import com.salgaderia.app.model.*;
public interface AutenticacaoService {
    Usuario login(String login,String senha);
    UsuarioCliente cadastrar(String email,String senha);
    UsuarioCliente atualizarPerfil(UsuarioCliente usuario,String apelido,String whatsapp,String endereco);
}
