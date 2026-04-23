package tarea.com;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class ej14 {
	  public void ejecutar() throws Exception {

		  BufferedReader lector = Utiles.getLector();
		  PrintWriter out = Utiles.getOut();
		  
        out.println("Ingrese una palabra:");
        String palabra = lector.readLine();

        StringBuilder sb = new StringBuilder(palabra);
        sb.insert(0, "<b>");
        sb.append("</b>");

        out.println(sb.toString());
    }
}