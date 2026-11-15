package com.salgaderia.app.model;

public abstract class Contato {
    private Long id;
    private String nome;
    private String telefone;
    private String observacoes;
    protected Contato(Long id, String nome, String telefone, String observacoes) {
        setId(id); setNome(nome); setTelefone(telefone); setObservacoes(observacoes);
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = Validacao.id(id); }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = Validacao.texto(nome, "nome"); }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    public abstract String getResumoContato();
}
