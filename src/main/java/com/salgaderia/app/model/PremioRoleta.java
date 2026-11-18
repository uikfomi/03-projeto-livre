package com.salgaderia.app.model;

import java.math.BigDecimal;

public record PremioRoleta(long pedidoId,Tipo tipo,String cupom,boolean novo) {
    public enum Tipo {
        PONTOS_20("+20 pontos",20,0), CUPOM_7("Cupom 7%",0,7),
        PONTOS_50("+50 pontos",50,0), CUPOM_10("Cupom 10%",0,10);
        private final String rotulo;
        private final int pontos;
        private final int percentual;
        Tipo(String rotulo,int pontos,int percentual){this.rotulo=rotulo;this.pontos=pontos;this.percentual=percentual;}
        public String rotulo(){return rotulo;}
        public int pontos(){return pontos;}
        public BigDecimal percentual(){return BigDecimal.valueOf(percentual);}
        public boolean temCupom(){return percentual>0;}
    }
}
