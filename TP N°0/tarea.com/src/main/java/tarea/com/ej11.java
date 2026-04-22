package tarea.com;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class ej11 {
	  public void ejecutar() throws Exception {

		  BufferedReader lector = Utiles.getLector();
		  PrintWriter out = Utiles.getOut();
		  
	        out.println("Nombre del producto:");
        String producto = lector.readLine();

        out.println("Cantidad comprada:");
        int cantidad = Integer.parseInt(lector.readLine());

        out.println("Precio unitario:");
        double precio = Double.parseDouble(lector.readLine());

        double total = cantidad * precio;

        StringBuilder recibo = new StringBuilder();
        recibo.append("Producto: ").append(producto)
              .append(" | Cantidad: ").append(cantidad)
              .append(" | Precio: $").append(precio)
              .append(" | Total: $").append(total);

        out.println(recibo.toString());
    }
}