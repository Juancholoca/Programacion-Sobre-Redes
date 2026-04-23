package tarea.com;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class ej9 {
	  public void ejecutar() throws Exception {

		  BufferedReader lector = Utiles.getLector();
		  PrintWriter out = Utiles.getOut();
		  
        out.println("Ingrese un texto:");
        String texto = lector.readLine();

        String resultado = "";

        for (int i = 0; i < texto.length(); i++) {
            if (i % 2 == 0)
                resultado += Character.toUpperCase(texto.charAt(i));
            else
                resultado += Character.toLowerCase(texto.charAt(i));
        }

        out.println(resultado);
    }
}