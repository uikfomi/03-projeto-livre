package com.salgaderia.app.service.pix;

import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class MercadoPagoPixGatewayTest {
    @Test void enviaJsonComIdempotenciaELerCobrancaPelaRotaCorreta() throws Exception {
        String referencia="a903f601-3361-4736-85f0-520978515eea";
        var observada=new AtomicReference<SolicitacaoHttp>();
        var servidor=HttpServer.create(new InetSocketAddress("127.0.0.1",0),0);
        servidor.createContext("/v1/payments",troca->{
            try(troca){
                var headers=troca.getRequestHeaders();
                String metodo=troca.getRequestMethod();
                observada.set(new SolicitacaoHttp(metodo,troca.getRequestURI().getPath(),headers.getFirst("Authorization"),
                        headers.getFirst("X-Idempotency-Key"),new String(troca.getRequestBody().readAllBytes(),StandardCharsets.UTF_8)));
                String status="POST".equals(metodo)?"pending":"approved";
                byte[] corpo=("""
                        {"id":123456,"status":"%s","transaction_amount":42.15,"currency_id":"BRL","payment_method_id":"pix",
                         "external_reference":"%s","point_of_interaction":{"transaction_data":{"qr_code":"000201BR-CODE"}}}
                        """).formatted(status,referencia).getBytes(StandardCharsets.UTF_8);
                troca.sendResponseHeaders(200,corpo.length);troca.getResponseBody().write(corpo);
            }
        });
        servidor.start();
        try {
            var cliente=new MercadoPagoPixGateway("APP_USR-teste",HttpClient.newHttpClient(),
                    URI.create("http://127.0.0.1:"+servidor.getAddress().getPort()+"/v1/payments"));
            var resposta=cliente.criar(new PixGatewayService.Solicitacao(referencia,new BigDecimal("42.15"),
                    "teste@example.invalid","12345678901",OffsetDateTime.parse("2026-11-19T10:20:30-03:00")));
            var post=observada.get();
            assertEquals("POST",post.metodo());assertEquals("Bearer APP_USR-teste",post.auth());
            assertEquals("/v1/payments",post.rota());assertEquals(referencia,post.idempotencia());
            var json=JsonParser.parseString(post.corpo()).getAsJsonObject();
            assertEquals(referencia,json.get("external_reference").getAsString());
            assertEquals(new BigDecimal("42.15"),json.get("transaction_amount").getAsBigDecimal());
            assertEquals("pix",json.get("payment_method_id").getAsString());
            assertEquals("CPF",json.getAsJsonObject("payer").getAsJsonObject("identification").get("type").getAsString());
            assertEquals("pending",resposta.status());assertEquals("000201BR-CODE",resposta.payload());
            assertEquals("approved",cliente.consultar(resposta.id()).status());
            assertEquals("/v1/payments/123456",observada.get().rota());assertEquals("GET",observada.get().metodo());
            assertThrows(IllegalArgumentException.class,()->cliente.consultar("../123"));
        }finally{servidor.stop(0);}
    }

    private record SolicitacaoHttp(String metodo,String rota,String auth,String idempotencia,String corpo) { }
}
