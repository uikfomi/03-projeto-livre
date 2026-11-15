package com.salgaderia.app.service;

import com.salgaderia.app.dao.ContatoDAO.FiltroContato;
import com.salgaderia.app.model.Contato;
import java.util.List;
import java.util.Optional;

public interface AgendaService {
    long cadastrar(Contato contato);
    boolean atualizar(Contato contato);
    boolean excluir(long id);
    Optional<Contato> buscarPorId(long id);
    List<Contato> buscar(FiltroContato filtro, String texto);
}
