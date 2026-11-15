package com.salgaderia.app.model;

import com.salgaderia.app.model.enums.PerfilUsuario;

public class UsuarioCliente extends Usuario {
    private Cliente cliente;
    public UsuarioCliente(Long id, String nome, String login, String senha, boolean ativo, Cliente cliente) {
        super(id, nome, login, senha, PerfilUsuario.CLIENTE, ativo);
        setCliente(cliente);
    }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = Validacao.obrigatorio(cliente, "cliente"); }
    @Override public String getTelaInicialFxml() { return "/fxml/cliente.fxml"; }
    @Override public boolean podeAcessarPainelGerencial() { return false; }
}
