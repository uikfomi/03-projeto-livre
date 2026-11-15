package com.salgaderia.app.util;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
/** Hash de senhas para novos cadastros; também reconhece as sementes legadas em texto puro. */
public final class Senhas {
    private static final String PREFIXO="pbkdf2-sha256";
    private static final int ITERACOES=210000;
    private Senhas() { }
    public static String gerar(String senha) {
        if(senha==null || senha.isBlank())throw new IllegalArgumentException("Senha obrigatória");
        byte[] salt=new byte[16];new SecureRandom().nextBytes(salt);
        return PREFIXO+"$"+ITERACOES+"$"+Base64.getEncoder().encodeToString(salt)+"$"+Base64.getEncoder().encodeToString(derivar(senha,salt,ITERACOES));
    }
    public static boolean legado(String hash) { return hash!=null&&!hash.startsWith(PREFIXO+"$"); }
    public static boolean verificar(String senha,String hash) {
        if(senha==null||hash==null)return false;
        if(legado(hash))return MessageDigest.isEqual(senha.getBytes(java.nio.charset.StandardCharsets.UTF_8),hash.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        try {String[] partes=hash.split("\\$",-1);if(partes.length!=4)return false;
            int iteracoes=Integer.parseInt(partes[1]); if(iteracoes<1||iteracoes>2000000)return false;
            byte[] salt=Base64.getDecoder().decode(partes[2]);byte[] esperado=Base64.getDecoder().decode(partes[3]);
            return MessageDigest.isEqual(esperado,derivar(senha,salt,iteracoes));
        } catch(IllegalArgumentException e){return false;}
    }
    private static byte[] derivar(String senha,byte[] salt,int iteracoes) {
        PBEKeySpec spec=new PBEKeySpec(senha.toCharArray(),salt,iteracoes,256);
        try{return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();}
        catch(GeneralSecurityException e){throw new IllegalStateException("PBKDF2 indisponível",e);}finally{spec.clearPassword();}
    }
}
