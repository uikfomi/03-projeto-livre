package com.salgaderia.app.model;

import com.salgaderia.app.model.enums.CategoriaInsumo;

public class Fornecedor extends Contato {
    private String nomeEmpresa;
    private String cnpj;
    private CategoriaInsumo categoriaInsumo;
    private int prazoEntregaDias;
    public Fornecedor(Long id, String nome, String telefone, String observacoes, String nomeEmpresa,
                      String cnpj, CategoriaInsumo categoriaInsumo, int prazoEntregaDias) {
        super(id, nome, telefone, observacoes);
        setNomeEmpresa(nomeEmpresa); setCnpj(cnpj); setCategoriaInsumo(categoriaInsumo); setPrazoEntregaDias(prazoEntregaDias);
    }
    public String getNomeEmpresa() { return nomeEmpresa; }
    public void setNomeEmpresa(String nomeEmpresa) { this.nomeEmpresa = Validacao.texto(nomeEmpresa, "nome da empresa"); }
    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) {
        String digits = Validacao.texto(cnpj, "CNPJ").replaceAll("[.\\-/]", "");
        if (!digits.matches("[0-9]{14}")) throw new IllegalArgumentException("CNPJ deve conter 14 dígitos");
        this.cnpj = digits;
    }
    public CategoriaInsumo getCategoriaInsumo() { return categoriaInsumo; }
    public void setCategoriaInsumo(CategoriaInsumo categoria) { this.categoriaInsumo = Validacao.obrigatorio(categoria, "categoria"); }
    public int getPrazoEntregaDias() { return prazoEntregaDias; }
    public void setPrazoEntregaDias(int dias) {
        if (dias < 0) throw new IllegalArgumentException("prazo não pode ser negativo");
        this.prazoEntregaDias = dias;
    }
    @Override public String getResumoContato() { return "Fornecedor: " + nomeEmpresa + " | Contato: " + getNome(); }
}
