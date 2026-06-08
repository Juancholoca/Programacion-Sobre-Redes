import java.io.*;

public class GestionArchivos {

    public static void main(String[] args) throws Exception {

        BufferedReader br =
                new BufferedReader(new InputStreamReader(System.in));

        PrintStream ps = System.out;

        int opcion;

        do {

            ps.println("\n===== MENU =====");
            ps.println("1 - Ultimo dato ingresado");
            ps.println("2 - Guardar numeros");
            ps.println("3 - Crear numeros.txt");
            ps.println("4 - Mostrar numeros.txt");
            ps.println("5 - Eliminar multiplos de 3");
            ps.println("6 - Crear primos.dat");
            ps.println("7 - Archivo caracteres.dat");
            ps.println("8 - Borrar lorem de HTML");
            ps.println("9 - Sistema clima");
            ps.println("0 - Salir");

            opcion = Integer.parseInt(br.readLine());

            switch(opcion) {

                case 1:
                    Ejercicio1.ejecutar();
                    break;

                case 2:
                    Ejercicio2.ejecutar();
                    break;

                case 3:
                    Ejercicio3.ejecutar();
                    break;

                case 4:
                    Ejercicio4.ejecutar();
                    break;

                case 5:
                    Ejercicio5.ejecutar();
                    break;

                case 6:
                    Ejercicio6.ejecutar();
                    break;

                case 7:
                    Ejercicio7.ejecutar();
                    break;

                case 8:
                    Ejercicio8.ejecutar();
                    break;

                case 9:
                    Ejercicio9.ejecutar();
                    break;
            }

        } while(opcion != 0);
    }
}