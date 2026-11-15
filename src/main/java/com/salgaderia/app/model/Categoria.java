package com.salgaderia.app.model;

public class Categoria {
    private Long id;
    private String nome;
    private String descricao;
    public Categoria(Long id, String nome, String descricao) { setId(id); setNome(nome); setDescricao(descricao); }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = Validacao.id(id); }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = Validacao.texto(nome, "nome"); }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
}
