package com.salgaderia.app.service;
import com.salgaderia.app.model.*;
import java.nio.file.Path;
import java.util.List;
public interface AvaliacaoService {
    java.util.Map<Long,java.math.BigDecimal> mediasPorProduto();
    java.util.List<AvaliacaoResumo> listar();
    List<AvaliacaoFoto> fotosRecentes();
    default long salvar(Avaliacao avaliacao) { return salvar(avaliacao, null); }
    long salvar(Avaliacao avaliacao, Path foto);
    boolean responder(Usuario admin,long avaliacaoId,String resposta);
    boolean excluir(Usuario admin,long avaliacaoId);
}
