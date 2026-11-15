package com.salgaderia.app.service.impl;

import com.salgaderia.app.dao.ContatoDAO;
import com.salgaderia.app.dao.ContatoDAO.FiltroContato;
import com.salgaderia.app.model.Contato;
import com.salgaderia.app.service.AgendaService;
import com.salgaderia.app.util.ConnectionFactory;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class AgendaServiceImpl implements AgendaService {
    private final ContatoDAO contatos;
    public AgendaServiceImpl(ContatoDAO contatos) { this.contatos = Objects.requireNonNull(contatos); }
    @Override public long cadastrar(Contato contato) {
        Objects.requireNonNull(contato);
        if (contato.getId() != null) throw new IllegalArgumentException("Contato já possui ID");
        long id = ConnectionFactory.executarEmTransacao(c -> contatos.inserir(c, contato));
        contato.setId(id);
        return id;
    }
    @Override public boolean atualizar(Contato contato) { return ConnectionFactory.executarEmTransacao(c -> contatos.atualizar(c, contato)); }
    @Override public boolean excluir(long id) { return ConnectionFactory.executarEmTransacao(c -> contatos.excluir(c, id)); }
    @Override public Optional<Contato> buscarPorId(long id) { return ConnectionFactory.executarEmTransacao(c -> contatos.buscarPorId(c, id)); }
    @Override public List<Contato> buscar(FiltroContato filtro, String texto) { return ConnectionFactory.executarEmTransacao(c -> contatos.buscar(c, filtro, texto)); }
}
