package FILES;

import java.io.*;
import java.io.File;

public class Ejercicio3 {

    public static void ejecutar() {

        PrintStream ps = System.out;
        PrintWriter pw = null;

        try {

            File archivo = new File("C:\\numeros.txt");

            if (!archivo.exists()) {
                archivo.createNewFile();
            }

            FileWriter fw = new FileWriter(archivo);
            pw = new PrintWriter(fw, true);

            for (int i = 0; i <= 1000; i++) {

                if (i % 2 == 0) {

                    pw.println(i);

                }
            }

            pw.flush();

            ps.println("Archivo numeros.txt creado correctamente.");

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