package com.salgaderia.app.model;

import java.time.LocalDateTime;

public class Avaliacao {
    private Long id;
    private final Usuario usuario;
    private final Produto produto;
    private int nota;
    private String comentario;
    private String respostaAdmin;
    private String fotoUrl;
    private final LocalDateTime criadoEm;
    public Avaliacao(Long id, Usuario usuario, Produto produto, int nota, String comentario,
                     String respostaAdmin, LocalDateTime criadoEm) {
        setId(id); this.usuario = Validacao.obrigatorio(usuario, "usuário");
        this.produto = Validacao.obrigatorio(produto, "produto"); setNota(nota);
        setComentario(comentario); setRespostaAdmin(respostaAdmin);
        this.criadoEm = Validacao.obrigatorio(criadoEm, "data de criação");
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = Validacao.id(id); }
    public Usuario getUsuario() { return usuario; }
    public Produto getProduto() { return produto; }
    public int getNota() { return nota; }
    public void setNota(int nota) {
        if (nota < 1 || nota > 5) throw new IllegalArgumentException("nota deve estar entre 1 e 5");
        this.nota = nota;
    }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
    public String getRespostaAdmin() { return respostaAdmin; }
    public void setRespostaAdmin(String respostaAdmin) { this.respostaAdmin = respostaAdmin; }
    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
}
