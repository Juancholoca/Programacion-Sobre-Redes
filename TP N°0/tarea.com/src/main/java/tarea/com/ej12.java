package tarea.com;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class ej12 {
	  public void ejecutar() throws Exception {

		  BufferedReader lector = Utiles.getLector();
		  PrintWriter out = Utiles.getOut();
		  
        out.println("Ingrese su nombre completo:");
        String nombre = lector.readLine();

        StringBuilder sb = new StringBuilder(nombre);
        sb.insert(0, "Dr./Dra. ");

        out.println(sb.toString());
    }
}