package tarea.com;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class ej5 {
	  public void ejecutar() throws Exception {

		  BufferedReader lector = Utiles.getLector();
		  PrintWriter out = Utiles.getOut();
		  
        out.println("Ingrese su email:");
        String email = lector.readLine();

        String usuario = email.substring(0, email.indexOf("@"));
        usuario = usuario.substring(0,1).toUpperCase() + usuario.substring(1).toLowerCase();

        out.println("Bienvenido " + usuario);
    }
}