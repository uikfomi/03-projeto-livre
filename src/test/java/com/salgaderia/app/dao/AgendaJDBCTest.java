package com.salgaderia.app.dao;

import com.salgaderia.app.dao.ContatoDAO.FiltroContato;
import com.salgaderia.app.dao.impl.ContatoDAOImpl;
import com.salgaderia.app.model.*;
import com.salgaderia.app.model.enums.CategoriaInsumo;
import com.salgaderia.app.util.ConnectionFactory;
import java.sql.Connection;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import static org.junit.jupiter.api.Assertions.*;

@EnabledIfEnvironmentVariable(named="SALGADERIA_TESTE_MYSQL", matches="true")
class AgendaJDBCTest {
    @Test void consultasEOperacoesSaoRevertidas() throws Exception {
        String token = UUID.randomUUID().toString().replace("-", "").substring(0, 20);
        ContatoDAO dao = new ContatoDAOImpl();
        try (Connection c = ConnectionFactory.getConnection()) {
            c.setAutoCommit(false);
            try {
                Cliente cliente = new Cliente(null, "Cliente " + token, null, "observacao " + token,
                        "w" + token, "endereco " + token, "ponto " + token);
                cliente.setId(dao.inserir(c, cliente));
                Fornecedor fornecedor = new Fornecedor(null, "Contato " + token, "tel" + token, null,
                        "Empresa " + token, "12345678000199", CategoriaInsumo.FARINHA, 2);
                fornecedor.setId(dao.inserir(c, fornecedor));
                assertEquals(2, dao.buscar(c, FiltroContato.TODOS, token).size());
                assertEquals(1, dao.buscar(c, FiltroContato.CLIENTES, "w" + token).size());
                assertEquals(1, dao.buscar(c, FiltroContato.CLIENTES, "endereco " + token).size());
                assertEquals(1, dao.buscar(c, FiltroContato.FORNECEDORES, "Empresa " + token).size());
                assertTrue(dao.buscar(c, FiltroContato.TODOS, token + "%").isEmpty());
                cliente.setNome("Editado " + token); assertTrue(dao.atualizar(c, cliente));
                assertEquals("Editado " + token, dao.buscarPorId(c, cliente.getId()).orElseThrow().getNome());
                assertTrue(dao.excluir(c, fornecedor.getId()));
                assertTrue(dao.buscarPorId(c, fornecedor.getId()).isEmpty());
            } finally { c.rollback(); }
        }
        try (Connection c = ConnectionFactory.getConnection()) {
            assertTrue(dao.buscar(c, FiltroContato.TODOS, token).isEmpty());
        }
    }
}
