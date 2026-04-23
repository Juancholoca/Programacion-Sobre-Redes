package tarea.com;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class ej4 {
	  public void ejecutar() throws Exception {

		  BufferedReader lector = Utiles.getLector();
		  PrintWriter out = Utiles.getOut();
		  
        out.println("Ingrese un párrafo:");
        String parrafo = lector.readLine();

        out.println("Palabra a buscar:");
        String buscar = lector.readLine();

        out.println("Palabra de reemplazo:");
        String reemplazo = lector.readLine();

        String resultado = parrafo.replace(buscar, reemplazo);

        out.println(resultado);
    }
}