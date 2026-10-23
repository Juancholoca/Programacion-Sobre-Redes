import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.Comparator;

/**
 * Servidor de Nube (Java 17).
 * Acepta conexiones TCP continuamente y entrega cada sesión a un hilo propio.
 */
public class ServidorNube {
    public static final int PUERTO = 5000;
    public static final Path CARPETA_NUBE = Paths.get("NUBE").toAbsolutePath().normalize();

    public static void main(String[] args) {
        try {
            Files.createDirectories(CARPETA_NUBE);
            try (ServerSocket servidor = new ServerSocket()) {
                servidor.setReuseAddress(true);
                servidor.bind(new InetSocketAddress(PUERTO));
                System.out.println("======================================");
                System.out.println("       SERVIDOR DE NUBE INICIADO");
                System.out.println("Puerto TCP: " + PUERTO);
                System.out.println("Carpeta compartida: " + CARPETA_NUBE);
                System.out.println("Esperando clientes... (Ctrl+C para salir)");
                System.out.println("======================================");

                while (true) {
                    Socket socketCliente = servidor.accept();
                    socketCliente.setSoTimeout(0);
                    System.out.println("[NUEVA CONEXIÓN] " +
                            socketCliente.getInetAddress().getHostAddress() +
                            ":" + socketCliente.getPort());

                    // Cada cliente se atiende en un hilo independiente.
                    Thread hilo = new Thread(
                            new ManejadorCliente(socketCliente, CARPETA_NUBE),
                            "Cliente-" + socketCliente.getInetAddress().getHostAddress());
                    hilo.start();
                }
            }
        } catch (IOException e) {
            System.err.println("No se pudo iniciar el servidor: " + e.getMessage());
        }
    }
}

/**
 * Maneja una sesión cliente. Los comandos y metadatos se transmiten con
 * DataInputStream/DataOutputStream; el contenido de los archivos se transmite
 * en bloques de bytes, sin convertirlo a texto.
 */
class ManejadorCliente implements Runnable {
    private static final String USUARIO_VALIDO = "admin";
    private static final String CLAVE_VALIDA = "nube123";
    private static final int TAM_BLOQUE = 8192;

    private final Socket socket;
    private final Path carpetaNube;
    private boolean autenticado = false;

    ManejadorCliente(Socket socket, Path carpetaNube) {
        this.socket = socket;
        this.carpetaNube = carpetaNube;
    }

    @Override
    public void run() {
        try (Socket s = socket;
             DataInputStream entrada = new DataInputStream(
                     new BufferedInputStream(s.getInputStream()));
             DataOutputStream salida = new DataOutputStream(
                     new BufferedOutputStream(s.getOutputStream()))) {

            salida.writeUTF("Conectado al Servidor de Nube. Inicie sesión con LOGIN.");
            salida.flush();

            boolean conectado = true;
            while (conectado) {
                String comando;
                try {
                    comando = entrada.readUTF().trim();
                } catch (EOFException e) {
                    break;
                }

                switch (comando.toUpperCase()) {
                    case "LOGIN" -> procesarLogin(entrada, salida);
                    case "LIST" -> procesarList(salida);
                    case "UPLOAD" -> procesarUpload(entrada, salida);
                    case "DOWNLOAD" -> procesarDownload(entrada, salida);
                    case "EXIT" -> {
                        salida.writeUTF("Sesión cerrada correctamente.");
                        salida.flush();
                        conectado = false;
                    }
                    default -> {
                        salida.writeUTF("ERROR: comando desconocido.");
                        salida.flush();
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("[SESIÓN FINALIZADA] " +
                    socket.getInetAddress().getHostAddress() + " - " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
            System.out.println("[DESCONECTADO] " + socket.getInetAddress().getHostAddress());
        }
    }

    private void procesarLogin(DataInputStream entrada, DataOutputStream salida)
            throws IOException {
        String usuario = entrada.readUTF();
        String clave = entrada.readUTF();

        // Credenciales de práctica: cambiar antes de usar en un entorno real.
        autenticado = USUARIO_VALIDO.equals(usuario) &&
                CLAVE_VALIDA.equals(clave);
        salida.writeBoolean(autenticado);
        salida.writeUTF(autenticado
                ? "Inicio de sesión correcto. Bienvenido, " + usuario + "."
                : "Usuario o clave incorrectos.");
        salida.flush();

        System.out.println("[LOGIN] " + socket.getInetAddress().getHostAddress()
                + " usuario=" + usuario + " resultado=" + (autenticado ? "OK" : "RECHAZADO"));
    }

    private void procesarList(DataOutputStream salida) throws IOException {
        if (!comprobarAutenticacion(salida)) return;

        try (var archivos = Files.list(carpetaNube)) {
            var lista = archivos.filter(Files::isRegularFile)
                    .sorted(Comparator.comparing(p -> p.getFileName().toString()))
                    .toList();
            salida.writeBoolean(true);
            salida.writeInt(lista.size());
            for (Path archivo : lista) {
                salida.writeUTF(archivo.getFileName().toString());
                salida.writeLong(Files.size(archivo));
            }
            salida.flush();
        } catch (IOException e) {
            salida.writeBoolean(false);
            salida.writeUTF("No se pudo leer la carpeta compartida.");
            salida.flush();
        }
    }

    private void procesarUpload(DataInputStream entrada, DataOutputStream salida)
            throws IOException {
        if (!comprobarAutenticacion(salida)) return;

        String nombreRecibido = entrada.readUTF();
        Path destino = resolverNombreSeguro(nombreRecibido);
        if (destino == null) {
            salida.writeBoolean(false);
            salida.writeUTF("Nombre de archivo no válido.");
            salida.flush();
            return;
        }

        salida.writeBoolean(true);
        salida.writeUTF("Nombre aceptado; puede comenzar la transferencia.");
        salida.flush();

        long tamano = entrada.readLong();
        if (tamano < 0) throw new IOException("Tamaño de archivo inválido.");

        Path temporal = Files.createTempFile(carpetaNube, "upload-", ".tmp");
        boolean completado = false;
        try (OutputStream archivo = new BufferedOutputStream(Files.newOutputStream(temporal))) {
            copiarExactamente(entrada, archivo, tamano);
            archivo.flush();
        } catch (IOException e) {
            Files.deleteIfExists(temporal);
            throw e;
        }

        try {
            // El archivo aparece con su nombre definitivo solo al terminar la copia.
            try {
                Files.move(temporal, destino, StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporal, destino, StandardCopyOption.REPLACE_EXISTING);
            }
            completado = true;
            salida.writeBoolean(true);
            salida.writeUTF("Archivo recibido: " + destino.getFileName() +
                    " (" + tamano + " bytes).");
            System.out.println("[UPLOAD] " + socket.getInetAddress().getHostAddress()
                    + " -> " + destino.getFileName() + " (" + tamano + " bytes)");
        } finally {
            if (!completado) Files.deleteIfExists(temporal);
        }
        salida.flush();
    }

    private void procesarDownload(DataInputStream entrada, DataOutputStream salida)
            throws IOException {
        if (!comprobarAutenticacion(salida)) return;

        String nombre = entrada.readUTF();
        Path archivo = resolverNombreSeguro(nombre);
        if (archivo == null || !Files.isRegularFile(archivo)) {
            salida.writeBoolean(false);
            salida.writeUTF("El archivo no existe o el nombre no es válido.");
            salida.flush();
            return;
        }

        long tamano = Files.size(archivo);
        salida.writeBoolean(true);
        salida.writeUTF("Descargando " + archivo.getFileName() + " (" + tamano + " bytes).");
        salida.writeLong(tamano);
        salida.flush();

        try (InputStream contenido = new BufferedInputStream(Files.newInputStream(archivo))) {
            copiarExactamente(contenido, salida, tamano);
        }
        salida.flush();
        System.out.println("[DOWNLOAD] " + socket.getInetAddress().getHostAddress()
                + " <- " + archivo.getFileName() + " (" + tamano + " bytes)");
    }

    /** En LIST/UPLOAD/DOWNLOAD, la autenticación se comprueba antes de operar. */
    private boolean comprobarAutenticacion(DataOutputStream salida) throws IOException {
        if (!autenticado) {
            salida.writeBoolean(false);
            salida.writeUTF("ERROR: primero debe autenticarse con LOGIN usuario clave.");
            salida.flush();
            return false;
        }
        return true;
    }

    /** Impide rutas como ../../archivo y acepta solamente un nombre simple. */
    private Path resolverNombreSeguro(String nombre) {
        if (nombre == null || nombre.isBlank()) return null;
        try {
            Path relativo = Paths.get(nombre);
            if (relativo.isAbsolute() || relativo.getNameCount() != 1 ||
                    nombre.equals(".") || nombre.equals("..") ||
                    nombre.contains("/") || nombre.contains("\\")) {
                return null;
            }
            Path destino = carpetaNube.resolve(relativo).normalize();
            return destino.getParent().equals(carpetaNube) ? destino : null;
        } catch (InvalidPathException e) {
            return null;
        }
    }

    /** Copia exactamente la cantidad indicada, incluso si el stream llega por partes. */
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
