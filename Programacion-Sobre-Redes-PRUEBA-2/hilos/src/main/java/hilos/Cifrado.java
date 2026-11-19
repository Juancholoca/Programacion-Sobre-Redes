package hilos;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class Cifrado {

	//TU-CODIGO-UNICO: 3BEAE84C    Validado
	
   private static final String CLAVE = "ClaveSecreta2026";

    private static final String ALGORITMO = "AES/CBC/PKCS5Padding";
    
    public static String encriptar(String texto) throws Exception{
    	return encriptar(texto,CLAVE);
    }

   public static String encriptar(String texto,String clave) throws Exception {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);

        Cipher cipher = Cipher.getInstance(ALGORITMO);
        SecretKeySpec key = new SecretKeySpec(clave.getBytes(StandardCharsets.UTF_8), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
        byte[] cifrado = cipher.doFinal(texto.getBytes(StandardCharsets.UTF_8));

        return Base64.getEncoder().encodeToString(iv) + ":" + Base64.getEncoder().encodeToString(cifrado);
    }

   public static String desencriptar(String linea) throws Exception {
        String[] partes = linea.split(":");
        if (partes.length != 2) {
            throw new IllegalArgumentException("Sin separador");
        }
        byte[] iv = Base64.getDecoder().decode(partes[0]);
        byte[] cifrado = Base64.getDecoder().decode(partes[1]);

        Cipher cipher = Cipher.getInstance(ALGORITMO);
        SecretKeySpec key = new SecretKeySpec(CLAVE.getBytes(StandardCharsets.UTF_8), "AES");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
        return new String(cipher.doFinal(cifrado), StandardCharsets.UTF_8);
    }

    public static String desencriptarONull(String linea) {
        try {
            return desencriptar(linea);
        } catch (Exception e) {
            return null;
        }
    }
}
