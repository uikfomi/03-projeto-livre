package com.salgaderia.app.util;

import com.salgaderia.app.exception.PersistenciaException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/** Gerencia o pool de conexões JDBC da aplicação. */
public final class ConnectionFactory {
    private static volatile HikariDataSource dataSourceCriado;

    private ConnectionFactory() {
    }

    private static HikariDataSource criarDataSource() {
        Properties propriedades = new Properties();
        ClassLoader loader = ConnectionFactory.class.getClassLoader();
        InputStream local = loader.getResourceAsStream("database.local.properties");
        try (InputStream arquivo = local != null ? local : loader.getResourceAsStream("database.properties")) {
            if (arquivo == null) {
                throw new IllegalStateException("Arquivo de configuração do banco não encontrado");
            }
            propriedades.load(arquivo);
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível ler a configuração do banco", e);
        }
        String url = propriedades.getProperty("url");
        if (url == null || url.isBlank()) {
            throw new IllegalStateException("A URL do banco não foi configurada");
        }
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(propriedades.getProperty("user", ""));
        config.setPassword(propriedades.getProperty("password", ""));
        HikariDataSource dataSource = new HikariDataSource(config);
        return dataSource;
    }

    public static Connection getConnection() throws SQLException {
        return obterDataSource().getConnection();
    }

    private static synchronized HikariDataSource obterDataSource() {
        if (dataSourceCriado == null || dataSourceCriado.isClosed()) {
            dataSourceCriado = criarDataSource();
        }
        return dataSourceCriado;
    }

    /** Executa a operação em uma conexão própria e confirma ou reverte a transação. */
    public static <T> T executarEmTransacao(OperacaoTransacional<T> operacao) {
        try (Connection conexao = getConnection()) {
            conexao.setAutoCommit(false);
            try {
                T resultado = operacao.executar(conexao);
                conexao.commit();
                return resultado;
            } catch (SQLException | RuntimeException | Error falha) {
                try {
                    conexao.rollback();
                } catch (SQLException erroRollback) {
                    falha.addSuppressed(erroRollback);
                }
                throw falha;
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Falha ao executar transação", e);
        }
    }

    /** Libera os recursos do pool no encerramento da aplicação. */
    public static synchronized void fechar() {
        HikariDataSource dataSource = dataSourceCriado;
        if (dataSource != null) {
            dataSource.close();
            dataSourceCriado = null;
        }
    }

    @FunctionalInterface
    public interface OperacaoTransacional<T> {
        T executar(Connection conexao) throws SQLException;
    }
}
