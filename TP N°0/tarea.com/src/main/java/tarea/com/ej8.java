package tarea.com;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class ej8 {
	  public void ejecutar() throws Exception {

		  BufferedReader lector = Utiles.getLector();
		  PrintWriter out = Utiles.getOut();
		  
        out.println("Ingrese la ruta del archivo:");
        String ruta = lector.readLine();

        String extension = ruta.substring(ruta.lastIndexOf(".") + 1);

        out.println("Extensión: " + extension);
    }
}