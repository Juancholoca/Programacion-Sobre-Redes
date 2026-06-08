package FILES;

import java.io.*;
import java.io.File;

public class Ejercicio6 {

    public static void ejecutar() {

        PrintStream ps = System.out;
        BufferedReader br = null;
        PrintWriter pw = null;

        try {

            File archivoOrigen = new File("C:\\numeros.txt");
            File archivoDestino = new File("C:\\primos.dat");

            if (!archivoOrigen.exists()) {

                ps.println("El archivo numeros.txt no existe.");
                return;

            }

            if (!archivoDestino.exists()) {

                archivoDestino.createNewFile();

            }

            FileReader fr = new FileReader(archivoOrigen);
            br = new BufferedReader(fr);

            FileWriter fw = new FileWriter(archivoDestino);
            pw = new PrintWriter(fw, true);

            String linea;

            while ((linea = br.readLine()) != null) {

                int numero = Integer.parseInt(linea);

                if (esPrimo(numero)) {

                    pw.println(numero);

                }
            }

            pw.flush();

            ps.println("Archivo primos.dat creado correctamente.");

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

    private static boolean esPrimo(int numero) {

        if (numero < 2) {

            return false;

        }

        for (int i = 2; i < numero; i++) {

            if (numero % i == 0) {

                return false;

            }
        }

        return true;
    }
}