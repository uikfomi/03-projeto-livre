package com.salgaderia.app.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record PlanoAssinatura(Long id, String nome, String descricao, BigDecimal mensalidade,
                              BigDecimal descontoPercentual, boolean ativo) {
    public static final String NOME_VIP="Clube VIP Ui K'Fomi";
    public static final BigDecimal DESCONTO_VIP=new BigDecimal("15.00");
    public static final BigDecimal MENSALIDADE_VIP=new BigDecimal("99.90");

    public PlanoAssinatura {
        Validacao.id(id);
        nome = Validacao.texto(nome, "nome do plano");
        descricao = Validacao.texto(descricao, "descrição do plano");
        if (nome.length() > 100 || descricao.length() > 500) throw new IllegalArgumentException("Nome ou descrição do plano muito longo.");
        mensalidade = decimal(mensalidade, new BigDecimal("99999999.99"), "mensalidade");
        descontoPercentual = decimal(descontoPercentual, new BigDecimal("100"), "percentual do plano");
    }

    private static BigDecimal decimal(BigDecimal valor, BigDecimal maximo, String campo) {
        if (valor == null || valor.signum() <= 0 || valor.compareTo(maximo) > 0 || valor.stripTrailingZeros().scale() > 2)
            throw new IllegalArgumentException("Confira o valor de " + campo + " (positivo, com até duas casas decimais).");
        return valor.setScale(2, RoundingMode.UNNECESSARY);
    }
}
