package FILES;

import java.io.*;
import java.io.File;

public class Ejercicio7 {

    public static void ejecutar() {

        BufferedReader brConsola = null;
        BufferedReader brArchivo = null;
        PrintStream ps = System.out;
        PrintWriter pw = null;

        try {

            brConsola = new BufferedReader(
                    new InputStreamReader(System.in));

            File archivo = new File("C:\\caracteres.dat");

            if (!archivo.exists()) {

                archivo.createNewFile();

            }

            FileWriter fw = new FileWriter(archivo);
            pw = new PrintWriter(fw, true);

            ps.println("Ingrese 10 palabras con la letra ñ:");

            for (int i = 1; i <= 10; i++) {

                ps.println("Palabra " + i + ":");

                String palabra = brConsola.readLine();

                pw.println(palabra);

            }

            pw.flush();
            pw.close();

            ps.println();
            ps.println("Fichero original:");

            FileReader fr = new FileReader(archivo);
            brArchivo = new BufferedReader(fr);

            String linea;
            String contenido = "";

            while ((linea = brArchivo.readLine()) != null) {

                ps.println(linea);

                contenido += linea.replace("ñ", "nie-nio") + "\n";

            }

            brArchivo.close();

            fw = new FileWriter(archivo);
            pw = new PrintWriter(fw, true);

            pw.print(contenido);

            pw.flush();
            pw.close();

            ps.println();
            ps.println("Fichero arreglado:");

            fr = new FileReader(archivo);
            brArchivo = new BufferedReader(fr);

            while ((linea = brArchivo.readLine()) != null) {

                ps.println(linea);

            }

        } catch (Exception e) {

            ps.println("Error: " + e.getMessage());

        } finally {

            try {

                if (brArchivo != null)
                    brArchivo.close();

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