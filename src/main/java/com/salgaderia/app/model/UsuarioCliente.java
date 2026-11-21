package com.salgaderia.app.model;

import com.salgaderia.app.model.enums.PerfilUsuario;

public class UsuarioCliente extends Usuario {
    private Cliente cliente;
    private final String email;
    public UsuarioCliente(Long id, String nome, String login, String senha, boolean ativo, Cliente cliente) {
        this(id,nome,login,senha,ativo,cliente,null);
    }
    public UsuarioCliente(Long id, String nome, String login, String senha, boolean ativo, Cliente cliente, String email) {
        super(id, nome, login, senha, PerfilUsuario.CLIENTE, ativo);
        this.email = email;
        setCliente(cliente);
    }
    public String getEmail() { return email; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = Validacao.obrigatorio(cliente, "cliente"); }
    @Override public String getTelaInicialFxml() { return "/fxml/cliente-vitrine.fxml"; }
    @Override public boolean podeAcessarPainelGerencial() { return false; }
}
