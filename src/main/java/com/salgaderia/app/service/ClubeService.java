package com.salgaderia.app.service;

import com.salgaderia.app.model.*;
import java.time.LocalDate;
import java.util.List;

public interface ClubeService {
    record Vitrine(List<PlanoAssinatura> planos,AssinaturaCliente assinatura,MensalidadeClube pendencia,LocalDate hoje) { }
    record Painel(List<PlanoAssinatura> planos,List<AssinaturaCliente> ativos,List<MensalidadeClube> pendencias) { }

    Vitrine vitrine(UsuarioCliente cliente);
    Painel painel(UsuarioAdmin admin);
    void salvarPlano(UsuarioAdmin admin,PlanoAssinatura plano);
    void solicitar(UsuarioCliente cliente,long planoId);
    void cancelar(UsuarioCliente cliente);
    void confirmar(UsuarioAdmin admin,long usuarioId,long mensalidadeId);
}
