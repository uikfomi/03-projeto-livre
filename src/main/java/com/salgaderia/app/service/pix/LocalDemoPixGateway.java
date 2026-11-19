package com.salgaderia.app.service.pix;

import com.salgaderia.app.service.PixPayloadService;

/** QR de demonstração não pagável; não produz estados approved. */
public final class LocalDemoPixGateway implements PixGatewayService {
    private final PixPayloadService local=new PixPayloadService(null,null,null);
    @Override public Cobranca criar(Solicitacao s){
        String txid=s.referencia().replace("-","").substring(0,24);
        return new Cobranca(null,"demo",local.gerarPayload(s.valor(),txid),s.valor(),"BRL","pix",s.referencia());
    }
    @Override public Cobranca consultar(String id){throw new Indisponivel("Demonstração não consulta transações reais.");}
}
