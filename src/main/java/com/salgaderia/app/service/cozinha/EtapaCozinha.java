package com.salgaderia.app.service.cozinha;

import com.salgaderia.app.model.enums.StatusPedido;
import com.salgaderia.app.model.enums.TipoEntrega;

/** Implementações State para cada situação persistida do pedido. */
public enum EtapaCozinha implements EstadoCozinha {
    MASSA("Massa na bancada",0.15,true),
    PREPARO("Fritando na hora / No forno",0.50,true),
    EMBALAGEM("Embalando quentinho",0.80,false),
    CONCLUIDO("Pronto",1.0,false) {
        @Override public String mensagem(TipoEntrega modalidade) {
            return modalidade==TipoEntrega.ENTREGA?"Saiu para entrega / Entregue":"Pronto no balcão / Entregue";
        }
    },
    CANCELADO("Pedido cancelado",0,false);
    private final String mensagem;private final double progresso;private final boolean aguarda;
    EtapaCozinha(String mensagem,double progresso,boolean aguarda){this.mensagem=mensagem;this.progresso=progresso;this.aguarda=aguarda;}
    @Override public String mensagem(TipoEntrega modalidade){return mensagem;}
    @Override public double progresso(){return progresso;}
    @Override public boolean aguardaCozinha(){return aguarda;}
    public static EstadoCozinha de(StatusPedido status){
        return switch(status){case PENDENTE->MASSA;case PREPARANDO->PREPARO;case PRONTO->EMBALAGEM;case ENTREGUE->CONCLUIDO;case CANCELADO->CANCELADO;};
    }
}
