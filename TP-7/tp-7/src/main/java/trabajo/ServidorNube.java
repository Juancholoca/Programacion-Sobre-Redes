package trabajo;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

/**
 * ServidorNube: queda escuchando de forma continua en un puerto TCP.
 *
 * MANEJO DE HILOS:
 * El hilo principal (main) solo ejecuta accept() en un bucle infinito.
 * Cada vez que llega una conexión, crea un nuevo Thread con un
 * ManejadorCliente y vuelve inmediatamente a accept(). Así un cliente
 * lento (por ejemplo subiendo un archivo grande) nunca bloquea a los demás.
 */
public class ServidorNube {

    public static final int PUERTO = 5000;
    public static final String CARPETA_NUBE = "nube_servidor";

    // Credenciales válidas (usuario -> clave).
    private static final Map<String, String> USUARIOS = Map.of(
            "juan", "1234",
            "admin", "admin",
            "invitado", "abcd");

    public static void main(String[] args) {
        // Carpeta local donde se alojan los archivos transferidos.
        File carpeta = new File(CARPETA_NUBE);
        if (!carpeta.exists() && !carpeta.mkdirs()) {
            System.err.println("No se pudo crear la carpeta " + CARPETA_NUBE);
            return;
        }

        // try-with-resources: el ServerSocket se cierra solo al salir.
        try (ServerSocket servidor = new ServerSocket(PUERTO)) {
            System.out.println("[SERVIDOR] Escuchando en el puerto " + PUERTO);
            System.out.println("[SERVIDOR] Carpeta de la nube: " + carpeta.getAbsolutePath());

            while (true) {
                // accept() bloquea hasta que un cliente se conecta.
                Socket socket = servidor.accept();
                System.out.println("[SERVIDOR] Nueva conexión desde "
                        + socket.getInetAddress().getHostAddress()
                        + ":" + socket.getPort());

                // Un hilo independiente por cliente.
                Thread hilo = new Thread(new ManejadorCliente(socket, carpeta, USUARIOS));
                hilo.start();
            }
        } catch (IOException e) {
            System.err.println("[SERVIDOR] Error: " + e.getMessage());
        }
    }
}