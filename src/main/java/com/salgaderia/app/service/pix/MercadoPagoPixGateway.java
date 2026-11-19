package com.salgaderia.app.service.pix;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.format.DateTimeFormatter;

/** Adapter da API Payments do Mercado Pago, sem SDK nem segredos em mensagens de erro. */
public final class MercadoPagoPixGateway implements PixGatewayService {
    private static final URI PAGAMENTOS = URI.create("https://api.mercadopago.com/v1/payments");
    private static final DateTimeFormatter EXPIRACAO=DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    private final HttpClient http;
    private final String token;
    private final URI pagamentos;

    public MercadoPagoPixGateway(String token) {
        this(token,HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(4))
                .followRedirects(HttpClient.Redirect.NEVER).build(),PAGAMENTOS);
    }

    MercadoPagoPixGateway(String token,HttpClient http,URI pagamentos) {
        if(token==null||token.isBlank()||!token.strip().matches("[!-~]+"))
            throw new IllegalArgumentException("Confira o access token do Mercado Pago.");
        this.token=token.strip();this.http=http;this.pagamentos=pagamentos;
    }

    @Override public Cobranca criar(Solicitacao s){
        JsonObject pedido=new JsonObject();pedido.addProperty("transaction_amount",s.valor());
        pedido.addProperty("description","Pedido Ui KFomi");
        pedido.addProperty("payment_method_id","pix");
        pedido.addProperty("external_reference",s.referencia());
        pedido.addProperty("date_of_expiration",s.expiraEm().format(EXPIRACAO));
        JsonObject pagador=new JsonObject();pagador.addProperty("email",s.email());
        if(s.documento()!=null){
            JsonObject identificacao=new JsonObject();identificacao.addProperty("type",s.documento().length()==11?"CPF":"CNPJ");
            identificacao.addProperty("number",s.documento());pagador.add("identification",identificacao);
        }
        pedido.add("payer",pagador);
        return enviar(requisicao(pagamentos).header("X-Idempotency-Key",s.referencia())
                .header("Content-Type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(pedido.toString(),StandardCharsets.UTF_8)).build());
    }

    @Override public Cobranca consultar(String id){
        if(id==null||!id.matches("[0-9]{1,100}"))throw new IllegalArgumentException("ID de transação PIX inválido.");
        return enviar(requisicao(URI.create(pagamentos+"/"+id)).GET().build());
    }

    private HttpRequest.Builder requisicao(URI uri){
        return HttpRequest.newBuilder(uri).timeout(Duration.ofSeconds(7))
                .header("Authorization","Bearer "+token).header("Accept","application/json");
    }

    private Cobranca enviar(HttpRequest requisicao){
        HttpResponse<String> resposta;
        try{
            resposta=http.send(requisicao,HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        }catch(IOException e){throw new Indisponivel("Sem conexão com o Mercado Pago. Nenhum pagamento foi confirmado.");
        }catch(InterruptedException e){Thread.currentThread().interrupt();throw new Indisponivel("Consulta PIX interrompida.");}
        int codigo=resposta.statusCode();
        if(codigo==429||codigo==408||codigo>=500)throw new Indisponivel("Mercado Pago temporariamente indisponível ("+codigo+").");
        if(codigo<200||codigo>=300)throw new RespostaInvalida("O Mercado Pago recusou a solicitação ("+codigo+"). Confira a configuração e os dados do pagador.");
        try{
            var json=JsonParser.parseString(resposta.body()).getAsJsonObject();
            String id=texto(json,"id"),status=texto(json,"status");
            var transacao=objeto(objeto(json,"point_of_interaction"),"transaction_data");
            var valor=json.get("transaction_amount");
            if(id==null||!id.matches("[0-9]{1,100}")||status==null||valor==null||valor.isJsonNull())throw new IllegalArgumentException();
            return new Cobranca(id,status,texto(transacao,"qr_code"),valor.getAsBigDecimal(),texto(json,"currency_id"),
                    texto(json,"payment_method_id"),texto(json,"external_reference"));
        }catch(RuntimeException e){throw new RespostaInvalida("A resposta PIX do Mercado Pago está incompleta ou não pôde ser validada.");}
    }
    private static JsonObject objeto(JsonObject json,String chave){
        JsonElement e=json==null?null:json.get(chave);
        return e!=null&&e.isJsonObject()?e.getAsJsonObject():null;
    }
    private static String texto(JsonObject json,String chave){
        JsonElement e=json==null?null:json.get(chave);
        return e!=null&&e.isJsonPrimitive()?e.getAsString():null;
    }
}
