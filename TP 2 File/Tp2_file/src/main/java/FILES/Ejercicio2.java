package FILES;

import java.io.*;

public class Ejercicio2 {

    public static void ejecutar() {

        BufferedReader br = null;
        PrintStream ps = System.out;
        PrintWriter pw = null;

        try {

            br = new BufferedReader(
                    new InputStreamReader(System.in));

            File archivo = new File("numerosIngresados.txt");

            if (!archivo.exists()) {
                archivo.createNewFile();
            }

            pw = new PrintWriter(
                    new FileWriter(archivo, true));

            String dato;

            ps.println("Ingrese datos (FIN para terminar)");

            while (true) {

                dato = br.readLine();

                if (dato.equalsIgnoreCase("FIN"))
                    break;

                try {

                    Double.parseDouble(dato);

                    pw.println(dato);

                } catch (Exception e) {

                }
            }

            pw.flush();

        } catch (Exception e) {

            ps.println(e.getMessage());

        } finally {

            try {
                if (pw != null)
                    pw.close();
            } catch (Exception e) {
            }
        }
    }
}