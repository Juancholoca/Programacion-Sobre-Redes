import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.Scanner;

/**
 * Cliente de Nube (Java 17).
 * Se conecta por TCP al servidor y permite escribir comandos desde la consola.
 */
public class ClienteNube {
    private static final int PUERTO = 5000;
    private static final int TAM_BLOQUE = 8192;
    private static final Path CARPETA_DESCARGAS =
            Paths.get("DESCARGAS").toAbsolutePath().normalize();

    public static void main(String[] args) {
        Scanner teclado = new Scanner(System.in);
        try {
            Files.createDirectories(CARPETA_DESCARGAS);
            System.out.print("IP del servidor (ejemplo 192.168.1.20): ");
            String ip = teclado.nextLine().trim();

            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(ip, PUERTO), 5000);
                socket.setSoTimeout(0);

                try (DataInputStream entrada = new DataInputStream(
                            new BufferedInputStream(socket.getInputStream()));
                     DataOutputStream salida = new DataOutputStream(
                            new BufferedOutputStream(socket.getOutputStream()))) {

                    System.out.println(entrada.readUTF());
                    System.out.println("Comandos: LOGIN usuario clave | LIST | UPLOAD ruta_archivo | "
                            + "DOWNLOAD nombre_archivo | EXIT");

                    boolean continuar = true;
                    while (continuar) {
                        System.out.print("\n nube> ");
                        String linea = teclado.nextLine().trim();
                        if (linea.isEmpty()) continue;

                        // Divide en comando y argumento; LOGIN admite usuario y clave.
                        String[] partes = linea.split("\\s+", 3);
                        String comando = partes[0].toUpperCase();

                        switch (comando) {
                            case "LOGIN" -> login(partes, entrada, salida);
                            case "LIST" -> listar(entrada, salida);
                            case "UPLOAD" -> subir(partes, entrada, salida);
                            case "DOWNLOAD" -> descargar(partes, entrada, salida);
                            case "EXIT" -> {
                                salida.writeUTF("EXIT");
                                salida.flush();
                                System.out.println(entrada.readUTF());
                                continuar = false;
                            }
                            default -> System.out.println("Comando no reconocido.");
                        }
                    }
                }
            }
        } catch (UnknownHostException e) {
            System.err.println("No se reconoce la IP ingresada.");
        } catch (SocketTimeoutException e) {
            System.err.println("No se pudo conectar: el servidor no respondió a tiempo.");
        } catch (ConnectException e) {
            System.err.println("No se pudo conectar. Verifique la IP, el puerto y el Firewall.");
        } catch (IOException e) {
            System.err.println("Error de comunicación: " + e.getMessage());
        }
        System.out.println("Cliente finalizado.");
    }

    private static void login(String[] partes, DataInputStream entrada, DataOutputStream salida)
            throws IOException {
        if (partes.length < 3) {
            System.out.println("Uso: LOGIN usuario clave");
            return;
        }
        salida.writeUTF("LOGIN");
        salida.writeUTF(partes[1]);
        salida.writeUTF(partes[2]);
        salida.flush();

        boolean ok = entrada.readBoolean();
        System.out.println(entrada.readUTF());
    }

    private static void listar(DataInputStream entrada, DataOutputStream salida)
            throws IOException {
        salida.writeUTF("LIST");
        salida.flush();
        boolean ok = entrada.readBoolean();
        if (!ok) {
            System.out.println(entrada.readUTF());
            return;
        }

        int cantidad = entrada.readInt();
        if (cantidad == 0) {
            System.out.println("La nube está vacía.");
            return;
        }
        System.out.println("Archivos disponibles:");
        for (int i = 0; i < cantidad; i++) {
            String nombre = entrada.readUTF();
            long tamano = entrada.readLong();
            System.out.printf(" - %s (%d bytes)%n", nombre, tamano);
        }
    }

    private static void subir(String[] partes, DataInputStream entrada, DataOutputStream salida)
            throws IOException {
        if (partes.length < 2) {
            System.out.println("Uso: UPLOAD ruta_archivo");
            return;
        }
        Path origen = Paths.get(partes[1]).toAbsolutePath().normalize();
        if (!Files.isRegularFile(origen)) {
            System.out.println("No se encuentra el archivo: " + origen);
            return;
        }

        salida.writeUTF("UPLOAD");
        salida.flush();
        boolean autenticado = entrada.readBoolean();
        String mensaje = entrada.readUTF();
        if (!autenticado) {
            System.out.println(mensaje);
            return;
        }

        String nombre = origen.getFileName().toString();
        salida.writeUTF(nombre);
        salida.flush();

        boolean nombreAceptado = entrada.readBoolean();
        System.out.println(entrada.readUTF());
        if (!nombreAceptado) return;

        long tamano = Files.size(origen);
        salida.writeLong(tamano);
        try (InputStream archivo = new BufferedInputStream(Files.newInputStream(origen))) {
            copiarExactamente(archivo, salida, tamano);
        }
        salida.flush();

        boolean ok = entrada.readBoolean();
        System.out.println(entrada.readUTF());
    }

    private static void descargar(String[] partes, DataInputStream entrada, DataOutputStream salida)
            throws IOException {
        if (partes.length < 2) {
            System.out.println("Uso: DOWNLOAD nombre_archivo");
            return;
        }
        String nombre = partes[1];

        salida.writeUTF("DOWNLOAD");
        salida.flush();
        boolean autenticado = entrada.readBoolean();
        String mensaje = entrada.readUTF();
        if (!autenticado) {
            System.out.println(mensaje);
            return;
        }

        salida.writeUTF(nombre);
        salida.flush();
        boolean disponible = entrada.readBoolean();
        String respuesta = entrada.readUTF();
        System.out.println(respuesta);
        if (!disponible) return;

        long tamano = entrada.readLong();
        Path destino = CARPETA_DESCARGAS.resolve(Paths.get(nombre).getFileName().toString()).normalize();
        Path temporal = Files.createTempFile(CARPETA_DESCARGAS, "download-", ".tmp");
        try {
            try (OutputStream archivo = new BufferedOutputStream(Files.newOutputStream(temporal))) {
                copiarExactamente(entrada, archivo, tamano);
            }
            try {
                Files.move(temporal, destino, StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporal, destino, StandardCopyOption.REPLACE_EXISTING);
            }
            System.out.println("Descarga completa: " + destino + " (" + tamano + " bytes).");
        } catch (IOException e) {
            Files.deleteIfExists(temporal);
            throw e;
        }
    }

    /** Envía/recibe los bytes sin alterarlos y respetando el tamaño informado. */
    private static void copiarExactamente(InputStream entrada, OutputStream salida, long cantidad)
            throws IOException {
        byte[] buffer = new byte[TAM_BLOQUE];
        long restantes = cantidad;
        while (restantes > 0) {
            int leidos = entrada.read(buffer, 0, (int) Math.min(buffer.length, restantes));
            if (leidos == -1) throw new EOFException("La transferencia quedó incompleta.");
            salida.write(buffer, 0, leidos);
            restantes -= leidos;
        }
    }
}
