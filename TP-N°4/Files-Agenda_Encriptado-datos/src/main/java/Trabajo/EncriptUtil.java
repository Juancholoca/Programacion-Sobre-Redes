package Trabajo;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class EncriptUtil {

private static final String CLAVE = "1234567890123456";

public static String encriptar(String texto) {

	try {

		SecretKeySpec key =	new SecretKeySpec(CLAVE.getBytes(), "AES");

		Cipher cipher =	Cipher.getInstance("AES");

		cipher.init(Cipher.ENCRYPT_MODE, key);

		byte[] datos = cipher.doFinal(texto.getBytes());

		return Base64.getEncoder().encodeToString(datos);

	} catch (Exception e) {

		e.printStackTrace();
		return "";
	}
}

public static String desencriptar(String textoEncriptado) {

	try {

		SecretKeySpec key =	new SecretKeySpec(CLAVE.getBytes(), "AES");

		Cipher cipher =	Cipher.getInstance("AES");

		cipher.init(Cipher.DECRYPT_MODE, key);

		byte[] datos =	Base64.getDecoder().decode(textoEncriptado);

		return new String(cipher.doFinal(datos));

	} catch (Exception e) {

		e.printStackTrace();
		return "";
	}
}

}
