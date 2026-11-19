package com.salgaderia.app.service.pix;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/** Porta de pagamento PIX; não marca pedidos como pagos por conta própria. */
public interface PixGatewayService {
    record Solicitacao(String referencia, BigDecimal valor, String email, String documento, OffsetDateTime expiraEm) { }
    record Cobranca(String id, String status, String payload, BigDecimal valor, String moeda,
                    String metodo, String referencia) { }

    Cobranca criar(Solicitacao solicitacao);
    Cobranca consultar(String id);

    final class Indisponivel extends RuntimeException {
        public Indisponivel(String mensagem) { super(mensagem); }
    }

    final class RespostaInvalida extends RuntimeException {
        public RespostaInvalida(String mensagem) { super(mensagem); }
    }
}
