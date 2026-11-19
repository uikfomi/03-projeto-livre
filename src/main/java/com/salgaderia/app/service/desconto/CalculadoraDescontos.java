package com.salgaderia.app.service.desconto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public final class CalculadoraDescontos {
    private final List<EstrategiaDesconto> estrategias = List.of(
            new DescontoCaixaMisteriosa(), new DescontoCupomPercentual(), new DescontoCentoSalgados(), new DescontoFidelidade(), new DescontoAssinante());

    public ResultadoDesconto calcular(ContextoDesconto contexto) {
        BigDecimal subtotal = contexto.subtotal().setScale(2, RoundingMode.HALF_UP);
        BigDecimal melhor = BigDecimal.ZERO.setScale(2);
        String nome = "NENHUM";
        for (EstrategiaDesconto estrategia : estrategias) {
            BigDecimal valor = estrategia.calcular(contexto);
            if (valor.compareTo(melhor) > 0) { melhor = valor; nome = estrategia.nome(); }
        }
        Cupom cupom=contexto.cupom();
        String codigo=nome.equals("CUPOM")?cupom.codigo():null;
        String brinde=null;
        if(cupom!=null&&subtotal.compareTo(cupom.minimo())>=0){
            // Benefícios sem abatimento só são escolhidos se forem a maior vantagem única.
            switch(cupom.beneficio()){
                case FRETE -> {
                    if(contexto.taxaEntrega().compareTo(melhor)>0){melhor=contexto.taxaEntrega();nome="FRETE";codigo=cupom.codigo();}
                }
                case BRINDE -> {
                    if(cupom.valorBeneficio().compareTo(melhor)>0){melhor=BigDecimal.ZERO.setScale(2);nome="BRINDE";codigo=cupom.codigo();brinde=cupom.brinde();}
                }
                case PONTOS_DOBRO -> {
                    if(nome.equals("NENHUM")){nome="PONTOS_DOBRO";codigo=cupom.codigo();}
                }
                default -> { }
            }
        }
        return new ResultadoDesconto(subtotal,melhor,subtotal.add(contexto.taxaEntrega()).subtract(melhor),nome,codigo,
                contexto.taxaEntrega(),brinde);
    }
}
