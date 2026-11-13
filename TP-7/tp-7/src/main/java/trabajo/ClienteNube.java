package trabajo;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * ClienteNube: se conecta al servidor por IP y ofrece una consola interactiva.
 *
 * Comandos: LOGIN <usuario> <clave> | LIST | UPLOAD <archivo> |
 * DOWNLOAD <archivo> | EXIT
 *
 * MANEJO DE STREAMS:
 * Se usan DataInputStream / DataOutputStream sobre el socket.
 * - Los comandos y respuestas viajan como texto (writeUTF / readUTF).
 * - Para subir/bajar archivos se envía primero el tamaño (long) y luego
 * los bytes en bloques de 4096, leyendo exactamente "tamaño" bytes.
 * El cliente es secuencial (un comando a la vez); la concurrencia la
 * aporta el servidor al atender cada cliente en un hilo propio.
 */
public class ClienteNube {

    private static final int PUERTO = 5000;
    private static final int TAM_BUFFER = 4096;
    private static final String CARPETA_DESCARGAS = "descargas_cliente";

    public static void main(String[] args) {
        Scanner teclado = new Scanner(System.in);
        System.out.print("IP del servidor: ");
        String ip = teclado.nextLine().trim();

        try (Socket socket = new Socket(ip, PUERTO);
             DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
             DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()))) {

            System.out.println("Conectado a " + ip + ":" + PUERTO);
            System.out.println("Comandos: LOGIN <usuario> <clave> | LIST | UPLOAD <archivo> | DOWNLOAD <archivo> | EXIT");

            boolean seguir = true;
            while (seguir) {
                System.out.print("> ");
                String linea = teclado.nextLine().trim();
                if (linea.isEmpty()) continue;

                String[] partes = linea.split("\\s+", 2);
                String comando = partes[0].toUpperCase();
                String argumento = partes.length > 1 ? partes[1].trim() : "";

                switch (comando) {
                    case "LOGIN" -> {
                        enviar(out, linea);
                        System.out.println(in.readUTF());
                    }
                    case "LIST" -> {
                        enviar(out, "LIST");
                        listar(in);
                    }
                    case "UPLOAD" -> subir(in, out, argumento);
                    case "DOWNLOAD" -> descargar(in, out, argumento);
                    case "EXIT" -> {
                        enviar(out, "EXIT");
                        System.out.println(in.readUTF());
                        seguir = false;
                    }
                    default -> System.out.println("Comando desconocido.");
                }
            }
            // Al salir del try-with-resources se cierran streams y socket limpiamente.
            System.out.println("Conexión cerrada.");

        } catch (IOException e) {
            System.err.println("Error de conexión: " + e.getMessage());
        }
    }

    private static void enviar(DataOutputStream out, String texto) throws IOException {
        out.writeUTF(texto);
        out.flush();
    }

    /** Muestra la respuesta de LIST: "OK n" y n líneas "nombre|bytes". */
    private static void listar(DataInputStream in) throws IOException {
        String cabecera = in.readUTF();
        if (!cabecera.startsWith("OK")) {
            System.out.println(cabecera);
            return;
        }
        int cantidad = Integer.parseInt(cabecera.split(" ")[1]);
        System.out.println("Archivos en la nube (" + cantidad + "):");
        for (int i = 0; i < cantidad; i++) {
            String[] datos = in.readUTF().split("\\|");
            System.out.printf(" %-30s %10s bytes%n", datos[0], datos[1]);
        }
    }

    private static void subir(DataInputStream in, DataOutputStream out, String nombre) throws IOException {
        if (nombre.isEmpty()) {
            System.out.println("Uso: UPLOAD <nombre_archivo>");
            return;
        }
        File archivo = new File(nombre);
        if (!archivo.isFile()) {
            System.out.println("El archivo local no existe: " + archivo.getAbsolutePath());
            return;
        }

        enviar(out, "UPLOAD " + nombre);
        String respuesta = in.readUTF();
        if (!respuesta.equals("READY")) {
            System.out.println(respuesta); // por ejemplo "ERROR: Debe autenticarse primero"
            return;
        }

        // Primero el tamaño, luego los bytes del archivo.
        out.writeLong(archivo.length());
        try (InputStream fuente = new BufferedInputStream(new FileInputStream(archivo))) {
            byte[] buffer = new byte[TAM_BUFFER];
            int leidos;
            while ((leidos = fuente.read(buffer)) != -1) {
                out.write(buffer, 0, leidos);
            }
        }
        out.flush();
        System.out.println(in.readUTF());
    }

    private static void descargar(DataInputStream in, DataOutputStream out, String nombre) throws IOException {
        if (nombre.isEmpty()) {
            System.out.println("Uso: DOWNLOAD <nombre_archivo>");
            return;
        }
        enviar(out, "DOWNLOAD " + nombre);
        String respuesta = in.readUTF();
        if (!respuesta.equals("OK")) {
            System.out.println(respuesta);
            return;
        }

        long tamanio = in.readLong();
        File carpeta = new File(CARPETA_DESCARGAS);
        carpeta.mkdirs();
        File destino = new File(carpeta, new File(nombre).getName());

        // Se leen exactamente "tamanio" bytes para reconstruir el archivo.
        try (OutputStream archivo = new BufferedOutputStream(new FileOutputStream(destino))) {
            byte[] buffer = new byte[TAM_BUFFER];
            long restante = tamanio;
            while (restante > 0) {
                int leidos = in.read(buffer, 0, (int) Math.min(buffer.length, restante));
                if (leidos == -1) throw new EOFException("Conexión cortada durante la descarga");
                archivo.write(buffer, 0, leidos);
                restante -= leidos;
            }
        }
        System.out.println("Descargado en " + destino.getAbsolutePath() + " (" + tamanio + " bytes)");
    }
}