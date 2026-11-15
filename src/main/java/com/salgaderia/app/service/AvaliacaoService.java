package com.salgaderia.app.service;
import com.salgaderia.app.model.*;
public interface AvaliacaoService {
    java.util.Map<Long,java.math.BigDecimal> mediasPorProduto();
    java.util.List<AvaliacaoResumo> listar();
    long salvar(Avaliacao avaliacao);
    boolean responder(Usuario admin,long avaliacaoId,String resposta);
    boolean excluir(Usuario admin,long avaliacaoId);
}
