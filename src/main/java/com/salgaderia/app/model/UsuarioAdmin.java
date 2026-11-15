package com.salgaderia.app.model;

import com.salgaderia.app.model.enums.PerfilUsuario;

public class UsuarioAdmin extends Usuario {
    public UsuarioAdmin(Long id, String nome, String login, String senha, boolean ativo) {
        super(id, nome, login, senha, PerfilUsuario.ADMIN, ativo);
    }
    @Override public String getTelaInicialFxml() { return "/fxml/selecao-modulos.fxml"; }
    @Override public boolean podeAcessarPainelGerencial() { return true; }
}
