package com.salgaderia.app.model;

/** Projeção pública de uma foto, sem nome nem outros dados da conta do cliente. */
public record AvaliacaoFoto(long id, long produtoId, String produto, int nota, String fotoUrl) { }
