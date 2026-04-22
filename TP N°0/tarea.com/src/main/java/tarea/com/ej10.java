package tarea.com;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class ej10 {
	  public void ejecutar() throws Exception {

		  BufferedReader lector = Utiles.getLector();
		  PrintWriter out = Utiles.getOut();
		  
        out.println("Texto principal:");
        String texto = lector.readLine();

        out.println("Fragmento a buscar:");
        String frag = lector.readLine();

        int inicio = texto.indexOf(frag);

        if (inicio != -1) {
            int fin = inicio + frag.length() - 1;
            out.println("Empieza en: " + inicio + " y termina en: " + fin);
        } else {
            out.println("No encontrado");
        }
    }
}