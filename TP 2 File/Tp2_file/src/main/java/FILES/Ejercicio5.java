package FILES;

import java.io.*;
import java.io.File;

public class Ejercicio5 {

    public static void ejecutar() {

        PrintStream ps = System.out;
        BufferedReader br = null;
        PrintWriter pw = null;

        try {

            File archivo = new File("C:\\numeros.txt");

            if (!archivo.exists()) {

                ps.println("El archivo numeros.txt no existe.");
                return;

            }

            FileReader fr = new FileReader(archivo);
            br = new BufferedReader(fr);

            String contenido = "";
            String linea;

            while ((linea = br.readLine()) != null) {

                int numero = Integer.parseInt(linea);

                if (numero % 3 != 0) {

                    contenido += numero + "\n";

                }
            }

            br.close();

            FileWriter fw = new FileWriter(archivo);
            pw = new PrintWriter(fw, true);

            pw.print(contenido);

            pw.flush();

            ps.println("Multiplos de 3 eliminados correctamente.");

        } catch (Exception e) {

            ps.println("Error: " + e.getMessage());

        } finally {

            try {

                if (br != null)
                    br.close();

            } catch (Exception e) {
            }

            try {

                if (pw != null)
                    pw.close();

            } catch (Exception e) {
            }
        }
    }
}