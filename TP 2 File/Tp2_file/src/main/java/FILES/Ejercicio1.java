package FILES;

import java.io.*;
import java.io.File;

public class Ejercicio1 {

    public static void ejecutar() {

        BufferedReader br = null;
        PrintStream ps = System.out;
        PrintWriter pw = null;

        try {

            br = new BufferedReader(
                    new InputStreamReader(System.in));

            File archivo = new File("ultimoDato.txt");

            if (!archivo.exists()) {
                archivo.createNewFile();
            }

            ps.println("Ingrese un dato:");

            String dato = br.readLine();


            FileWriter fw = new FileWriter(archivo);
            pw = new PrintWriter(fw, true);

            pw.println(dato);

            pw.flush();

            ps.println("Dato guardado correctamente.");

        } catch (Exception e) {

            ps.println("Error: " + e.getMessage());

        } finally {

            try {
                if (pw != null)
                    pw.close();
            } catch (Exception e) {
            }
        }
    }
}