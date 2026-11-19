package com.salgaderia.app.service.pix;

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/** Configuração local, não versionada; sem acessar o banco ou o provedor durante a leitura. */
public final class PixConfiguracao {
    private static final Properties LOCAL=carregar();
    private PixConfiguracao() { }

    public static boolean temMercadoPago(){return !LOCAL.getProperty("mercadopago.access.token","").isBlank();}
    public static PixGatewayService gatewayReal(){return new MercadoPagoPixGateway(LOCAL.getProperty("mercadopago.access.token"));}
    public static BigDecimal taxaEntrega(){
        String configurada=LOCAL.getProperty("entrega.taxa","5.00");
        try{
            var valor=new BigDecimal(configurada.strip()).setScale(2,RoundingMode.UNNECESSARY);
            if(valor.signum()<0||valor.compareTo(new BigDecimal("99999999.99"))>0)throw new ArithmeticException();
            return valor;
        }catch(ArithmeticException|NumberFormatException e){throw new IllegalArgumentException("Confira a propriedade entrega.taxa (valor não negativo com até duas casas decimais).");}
    }
    private static Properties carregar(){
        var propriedades=new Properties();
        try(var arquivo=PixConfiguracao.class.getClassLoader().getResourceAsStream("database.local.properties")){
            if(arquivo!=null)propriedades.load(new InputStreamReader(arquivo,StandardCharsets.UTF_8));
            return propriedades;
        }catch(IOException e){throw new IllegalStateException("Não foi possível ler as configurações locais do PIX.",e);}
    }
}
