package com.salgaderia.app.model;

public class Cliente extends Contato {
    private String whatsapp;
    private String endereco;
    private String pontoReferencia;
    public Cliente(Long id, String nome, String telefone, String observacoes, String whatsapp,
                   String endereco, String pontoReferencia) {
        super(id, nome, telefone, observacoes);
        setWhatsapp(whatsapp); setEndereco(endereco); setPontoReferencia(pontoReferencia);
    }
    public String getWhatsapp() { return whatsapp; }
    public void setWhatsapp(String whatsapp) { this.whatsapp = whatsapp; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public String getPontoReferencia() { return pontoReferencia; }
    public void setPontoReferencia(String pontoReferencia) { this.pontoReferencia = pontoReferencia; }
    @Override public String getResumoContato() {
        return "Cliente: " + getNome() + (whatsapp == null || whatsapp.isBlank() ? "" : " | WhatsApp: " + whatsapp);
    }
}
