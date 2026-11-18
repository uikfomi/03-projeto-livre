package com.salgaderia.app.model;

import java.time.LocalDateTime;

/** Um selo desbloqueado para uma conta. */
public record ConquistaCliente(String codigo,String nome,String descricao,LocalDateTime desbloqueadaEm) { }
