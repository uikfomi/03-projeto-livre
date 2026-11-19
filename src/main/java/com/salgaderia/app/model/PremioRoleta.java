package com.salgaderia.app.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import com.salgaderia.app.service.desconto.Cupom.Beneficio;

public record PremioRoleta(long pedidoId,Tipo tipo,String cupom,boolean novo,boolean vip,boolean giroExtra,LocalDateTime registradoEm) {
    public static final BigDecimal MINIMO=new BigDecimal("25.00");
    public static final BigDecimal LIMITE_VIP=new BigDecimal("60.00");
    public PremioRoleta(long pedidoId,Tipo tipo,String cupom,boolean novo){
        this(pedidoId,tipo,cupom,novo,false,tipo==Tipo.GIRO_EXTRA,null);
    }
    public LocalDateTime validoAte(){return registradoEm==null?null:registradoEm.plusDays(5);}
    public static List<Tipo> fatias(boolean vip){
        return vip?List.of(Tipo.COMBO_BRINDE,Tipo.FRETE_GRATIS,Tipo.PONTOS_DOBRO,Tipo.CUPOM_15,Tipo.GIRO_EXTRA,Tipo.BRINDE_MINI):
                List.of(Tipo.BRINDE_MINI,Tipo.FRETE_GRATIS,Tipo.PONTOS_DOBRO,Tipo.CUPOM_10,Tipo.GIRO_EXTRA,Tipo.SEM_PREMIO);
    }
    public enum Tipo {
        PONTOS_20("+20 pontos",20,null,0,0), CUPOM_7("Cupom 7%",0,Beneficio.PERCENTUAL,7,0),
        PONTOS_50("+50 pontos",50,null,0,0), CUPOM_10("Cupom 10% OFF",0,Beneficio.PERCENTUAL,10,0),
        BRINDE_MINI("Mini porção surpresa",0,Beneficio.BRINDE,0,4),
        FRETE_GRATIS("Frete grátis",0,Beneficio.FRETE,0,0),
        PONTOS_DOBRO("Pontos em dobro",0,Beneficio.PONTOS_DOBRO,0,0),
        GIRO_EXTRA("Giro extra",0,null,0,0), SEM_PREMIO("Não foi dessa vez",0,null,0,0),
        CUPOM_15("Cupom VIP 15% OFF",0,Beneficio.PERCENTUAL,15,0),
        COMBO_BRINDE("Combo brinde VIP",0,Beneficio.BRINDE,0,8);
        private final String rotulo;
        private final int pontos;
        private final int percentual;
        private final Beneficio beneficio;
        private final int valorBeneficio;
        Tipo(String rotulo,int pontos,Beneficio beneficio,int percentual,int valorBeneficio){
            this.rotulo=rotulo;this.pontos=pontos;this.percentual=percentual;this.beneficio=beneficio;this.valorBeneficio=valorBeneficio;
        }
        public String rotulo(){return rotulo;}
        public int pontos(){return pontos;}
        public BigDecimal percentual(){return BigDecimal.valueOf(percentual);}
        public boolean temCupom(){return beneficio!=null;}
        public Beneficio beneficio(){return beneficio;}
        public BigDecimal valorBeneficio(){return BigDecimal.valueOf(valorBeneficio);}
    }
}
