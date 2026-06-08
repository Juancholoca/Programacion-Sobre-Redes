package FILES;

import java.io.*;
import java.io.File;

public class Ejercicio4 {

    public static void ejecutar() {

        PrintStream ps = System.out;
        BufferedReader br = null;

        try {

            File archivo = new File("C:\\numeros.txt");

            if (!archivo.exists()) {

                ps.println("El archivo numeros.txt no existe.");
                return;

            }

            FileReader fr = new FileReader(archivo);

            br = new BufferedReader(fr);

            String linea;

            ps.println("Contenido de numeros.txt:");
            ps.println();

            while ((linea = br.readLine()) != null) {

                ps.println(linea);

            }

        } catch (Exception e) {

            ps.println("Error: " + e.getMessage());

        } finally {

            try {

                if (br != null)
                    br.close();

            } catch (Exception e) {
            }
        }
    }
}