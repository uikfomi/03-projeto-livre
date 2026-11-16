package com.salgaderia.app.service;

import com.salgaderia.app.model.Usuario;
import com.salgaderia.app.model.comanda.*;
import com.salgaderia.app.model.enums.FormaPagamento;
import java.math.BigDecimal;
import java.util.List;

public interface ComandaService {
    List<Mesa> listarMesas();
    Comanda abrirOuConsultar(long mesaId,Usuario usuario);
    Comanda consultar(long mesaId,long atendimentoId,Usuario usuario);
    Comanda adicionarItem(long mesaId,long atendimentoId,long produtoId,BigDecimal quantidade,Usuario usuario);
    /** Retorna true quando o pagamento zerou o saldo e liberou a mesa. */
    boolean pagarValor(long mesaId,long atendimentoId,String pessoa,FormaPagamento forma,BigDecimal valor,Usuario usuario);
    boolean pagarItem(long mesaId,long atendimentoId,String pessoa,FormaPagamento forma,long itemId,BigDecimal quantidade,Usuario usuario);
    List<BigDecimal> dividirIgualmente(BigDecimal saldo,int pessoas);
    void fecharSemSaldo(long mesaId,long atendimentoId,Usuario usuario);
}
