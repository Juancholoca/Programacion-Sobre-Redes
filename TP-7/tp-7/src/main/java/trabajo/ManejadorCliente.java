package trabajo;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Map;

/**
 * ManejadorCliente: atiende a UN cliente dentro de su propio hilo.
 *
 * MANEJO DE HILOS:
 * Implementa Runnable. Cada instancia tiene su propio socket, sus propios
 * streams y su propio estado (autenticado), por lo que las sesiones no
 * comparten datos mutables y no se interfieren entre sí.
 * Para evitar que dos subidas simultáneas del mismo nombre corrompan el
 * archivo, cada subida se escribe en un archivo temporal único y luego se
 * mueve al destino final (operación atómica).
 *
 * MANEJO DE STREAMS:
 * Se usan DataInputStream / DataOutputStream sobre el socket:
 * - writeUTF / readUTF para comandos y respuestas de texto.
 * - writeLong / readLong para anunciar el tamaño de un archivo.
 * - write / read con un buffer de bytes para transferir el contenido.
 * Como se envía el tamaño antes de los bytes, el receptor sabe exactamente
 * cuántos bytes leer y no se mezclan con los comandos siguientes.
 */
public class ManejadorCliente implements Runnable {

    private static final int TAM_BUFFER = 4096;

    private final Socket socket;
    private final File carpeta;
    private final Map<String, String> usuarios;
    private final String ip;

    private boolean autenticado = false;
    private String usuarioActual = "(anónimo)";

    public ManejadorCliente(Socket socket, File carpeta, Map<String, String> usuarios) {
        this.socket = socket;
        this.carpeta = carpeta;
        this.usuarios = usuarios;
        this.ip = socket.getInetAddress().getHostAddress();
    }

    @Override
    public void run() {
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
             DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()))) {

            boolean seguir = true;
            while (seguir) {
                String linea = in.readUTF(); // bloquea hasta recibir un comando
                String[] partes = linea.trim().split("\\s+", 3);
                String comando = partes[0].toUpperCase();
                log("Comando recibido: " + linea.replaceAll("(?i)^(LOGIN\\s+\\S+\\s+).*", "$1****"));

                // Antes de autenticarse solo se permite LOGIN y EXIT.
                if (!autenticado && !comando.equals("LOGIN") && !comando.equals("EXIT")) {
                    responder(out, "ERROR: Debe autenticarse primero con LOGIN <usuario> <clave>");
                    continue;
                }

                switch (comando) {
                    case "LOGIN" -> login(out, partes);
                    case "LIST" -> listar(out);
                    case "UPLOAD" -> subir(in, out, nombreArgumento(linea));
                    case "DOWNLOAD" -> descargar(out, nombreArgumento(linea));
                    case "EXIT" -> {
                        responder(out, "OK: Sesión finalizada");
                        seguir = false;
                    }
                    default -> responder(out, "ERROR: Comando desconocido");
                }
            }
        } catch (EOFException e) {
            log("El cliente cerró la conexión sin enviar EXIT");
        } catch (IOException e) {
            log("Error de comunicación: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException ignorado) {
            }
            log("Sesión cerrada");
        }
    }

    // ---------------------------------------------------------------- comandos

    private void login(DataOutputStream out, String[] partes) throws IOException {
        if (partes.length < 3) {
            responder(out, "ERROR: Uso: LOGIN <usuario> <clave>");
            return;
        }
        String clave = usuarios.get(partes[1]);
        if (clave != null && clave.equals(partes[2])) {
            autenticado = true;
            usuarioActual = partes[1];
            responder(out, "OK: Bienvenido " + usuarioActual);
            log("Login correcto");
        } else {
            responder(out, "ERROR: Usuario o clave incorrectos");
            log("Login fallido para '" + partes[1] + "'");
        }
    }

    /** LIST: envía "OK <cantidad>" y luego una línea "nombre|bytes" por archivo. */
    private void listar(DataOutputStream out) throws IOException {
        File[] archivos = carpeta.listFiles(f -> f.isFile() && !f.getName().endsWith(".tmp"));
        if (archivos == null) archivos = new File[0];
        Arrays.sort(archivos);
        out.writeUTF("OK " + archivos.length);
        for (File f : archivos) {
            out.writeUTF(f.getName() + "|" + f.length());
        }
        out.flush();
        log("LIST enviado (" + archivos.length + " archivos)");
    }

    /**
     * UPLOAD: protocolo
     * servidor -> "READY"
     * cliente -> long tamaño + bytes
     * servidor -> "OK ..." o "ERROR ..."
     */
    private void subir(DataInputStream in, DataOutputStream out, String nombre) throws IOException {
        String limpio = limpiarNombre(nombre);
        if (limpio == null) {
            responder(out, "ERROR: Nombre de archivo inválido");
            return;
        }
        responder(out, "READY");

        long tamanio = in.readLong();
        Path temporal = Files.createTempFile(carpeta.toPath(), "subida_", ".tmp");
        Path destino = new File(carpeta, limpio).toPath();

        try (OutputStream archivo = new BufferedOutputStream(Files.newOutputStream(temporal))) {
            byte[] buffer = new byte[TAM_BUFFER];
            long restante = tamanio;
            while (restante > 0) {
                int leidos = in.read(buffer, 0, (int) Math.min(buffer.length, restante));
                if (leidos == -1) throw new EOFException("Conexión cortada durante la subida");
                archivo.write(buffer, 0, leidos); // byte a byte (en bloques) hacia el disco
                restante -= leidos;
            }
        } catch (IOException e) {
            Files.deleteIfExists(temporal); // no dejar archivos a medias
            throw e;
        }

        Files.move(temporal, destino, StandardCopyOption.REPLACE_EXISTING);
        responder(out, "OK: '" + limpio + "' subido (" + tamanio + " bytes)");
        log("UPLOAD completo: " + limpio + " (" + tamanio + " bytes)");
    }

    /**
     * DOWNLOAD: protocolo
     * servidor -> "OK" + long tamaño + bytes (o "ERROR ...")
     */
    private void descargar(DataOutputStream out, String nombre) throws IOException {
        String limpio = limpiarNombre(nombre);
        File archivo = (limpio == null) ? null : new File(carpeta, limpio);
        if (archivo == null || !archivo.isFile()) {
            responder(out, "ERROR: El archivo no existe en la nube");
            return;
        }

        out.writeUTF("OK");
        out.writeLong(archivo.length());
        try (InputStream fuente = new BufferedInputStream(new FileInputStream(archivo))) {
            byte[] buffer = new byte[TAM_BUFFER];
            int leidos;
            while ((leidos = fuente.read(buffer)) != -1) {
                out.write(buffer, 0, leidos);
            }
        }
        out.flush();
        log("DOWNLOAD completo: " + limpio + " (" + archivo.length() + " bytes)");
    }

    // ---------------------------------------------------------------- utilidades

    private void responder(DataOutputStream out, String mensaje) throws IOException {
        out.writeUTF(mensaje);
        out.flush();
    }

    /** Devuelve todo lo que sigue al comando (permite nombres con espacios). */
    private String nombreArgumento(String linea) {
        String[] p = linea.trim().split("\\s+", 2);
        return p.length > 1 ? p[1].trim() : "";
    }

    /** Evita rutas como ../../x: solo se acepta el nombre simple del archivo. */
    private String limpiarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) return null;
        String simple = new File(nombre).getName();
        return simple.isBlank() ? null : simple;
    }

    private void log(String mensaje) {
        System.out.println("[" + Thread.currentThread().getName() + "] "
                + ip + " (" + usuarioActual + "): " + mensaje);
    }
}
