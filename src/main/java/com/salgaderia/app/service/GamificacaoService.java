package com.salgaderia.app.service;

import com.salgaderia.app.model.ConquistaCliente;
import com.salgaderia.app.model.PremioRoleta;
import com.salgaderia.app.model.UsuarioCliente;
import java.util.List;

public interface GamificacaoService {
    record Perfil(List<ConquistaCliente> selos,long pontos,List<String> cupons) { }
    Perfil perfil(UsuarioCliente cliente);
    PremioRoleta girar(UsuarioCliente cliente,long pedidoId);
}
