package com.salgaderia.app.model;

import com.salgaderia.app.model.enums.PerfilUsuario;

public abstract class Usuario {
    private Long id;
    private String nome;
    private String login;
    private String senha;
    private final PerfilUsuario perfil;
    private boolean ativo;

    protected Usuario(Long id, String nome, String login, String senha, PerfilUsuario perfil, boolean ativo) {
        setId(id); setNome(nome); setLogin(login); setSenha(senha);
        this.perfil = Validacao.obrigatorio(perfil, "perfil");
        this.ativo = ativo;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = Validacao.id(id); }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = Validacao.texto(nome, "nome"); }
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = Validacao.texto(login, "login"); }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = Validacao.texto(senha, "senha"); }
    public PerfilUsuario getPerfil() { return perfil; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
    public abstract String getTelaInicialFxml();
    public abstract boolean podeAcessarPainelGerencial();
}
