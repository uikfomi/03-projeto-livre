package com.salgaderia.app.service;

import com.salgaderia.app.dao.ConfiguracaoLojaDAO;
import com.salgaderia.app.dao.impl.ConfiguracaoLojaDAOImpl;
import com.salgaderia.app.model.ConfiguracaoLoja;
import com.salgaderia.app.util.ConnectionFactory;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public final class OperacaoLojaService {
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");
    private final ConfiguracaoLojaDAO dao;
    private final Clock relogio;

    public OperacaoLojaService() { this(new ConfiguracaoLojaDAOImpl(), Clock.system(ZoneId.of("America/Fortaleza"))); }
    public OperacaoLojaService(ConfiguracaoLojaDAO dao, Clock relogio) {
        this.dao = Objects.requireNonNull(dao);
        this.relogio = Objects.requireNonNull(relogio);
    }
    public ConfiguracaoLoja consultar() { return ConnectionFactory.executarEmTransacao(c -> dao.buscar(c, false)); }
    public void salvarExpediente(LocalTime abertura, LocalTime fechamento, boolean automatico) {
        var config = new ConfiguracaoLoja(true, abertura, fechamento, automatico);
        ConnectionFactory.executarEmTransacao(c -> { dao.salvarExpediente(c, config); return null; });
    }
    public void definirAberturaManual(boolean aberta) {
        ConnectionFactory.executarEmTransacao(c -> { dao.definirAberturaManual(c, aberta); return null; });
    }
    public boolean estaAberta(ConfiguracaoLoja config) { return estaAberta(config, LocalTime.now(relogio)); }
    public static boolean dentroDoExpediente(ConfiguracaoLoja config, LocalTime hora) {
        boolean aposAbertura = !hora.isBefore(config.abertura());
        boolean antesFechamento = hora.isBefore(config.fechamento());
        return config.abertura().isBefore(config.fechamento()) ? aposAbertura && antesFechamento : aposAbertura || antesFechamento;
    }
    public static boolean estaAberta(ConfiguracaoLoja config, LocalTime hora) {
        return config.abertaManualmente() && (!config.respeitarHorarioAutomatico() || dentroDoExpediente(config, hora));
    }
    public String rotulo(ConfiguracaoLoja config) {
        if (estaAberta(config)) return "🟢 Loja Aberta";
        if (!config.abertaManualmente()) return "🔴 Loja Fechada no momento — fechamento manual";
        return "🔴 Loja Fechada no momento — Abre às " + config.abertura().format(HORA);
    }
    public void validarPedido(ConfiguracaoLoja config, LocalDateTime agendado) {
        var agora = LocalDateTime.now(relogio);
        if (agendado == null) {
            if (!estaAberta(config, agora.toLocalTime()))
                throw new IllegalArgumentException("A loja está fechada para pedidos imediatos. Você pode agendar dentro do expediente.");
        } else if (!agendado.isAfter(agora) || !dentroDoExpediente(config, agendado.toLocalTime())) {
            throw new IllegalArgumentException("Escolha uma data futura dentro do expediente (" + config.abertura().format(HORA)
                    + " às " + config.fechamento().format(HORA) + ").");
        }
    }
    public void validarPedido(Connection c, LocalDateTime agendado) throws SQLException {
        validarPedido(dao.buscar(c, true), agendado);
    }
}
