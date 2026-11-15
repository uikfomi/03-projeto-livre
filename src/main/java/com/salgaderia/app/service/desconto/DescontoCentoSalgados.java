package com.salgaderia.app.service.desconto;

import com.salgaderia.app.model.Categoria;
import com.salgaderia.app.model.PedidoItem;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.util.Locale;

public final class DescontoCentoSalgados implements EstrategiaDesconto {
    @Override public BigDecimal calcular(ContextoDesconto contexto) {
        var elegiveis = contexto.itens().stream().filter(i -> salgado(i.getProduto().getCategoria())).toList();
        BigDecimal quantidade = elegiveis.stream().map(PedidoItem::getQuantidade).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (quantidade.compareTo(new BigDecimal("100")) < 0) return BigDecimal.ZERO;
        return elegiveis.stream().map(PedidoItem::calcularSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add)
                .multiply(new BigDecimal("0.12")).setScale(2, RoundingMode.HALF_UP);
    }
    private boolean salgado(Categoria categoria) {
        if (categoria == null) return false;
        String nome = Normalizer.normalize(categoria.getNome(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "").toLowerCase(Locale.ROOT);
        if (nome.contains("bebid") || nome.contains("doc")) return false;
        return nome.contains("salgad") || nome.contains("frit") || nome.contains("assad");
    }
    @Override public String nome() { return "CENTO"; }
}
