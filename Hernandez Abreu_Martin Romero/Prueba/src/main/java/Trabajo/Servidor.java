package Trabajo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Servidor {

    static final String RESET = "\u001B[0m";
    static final String VERDE = "\u001B[32m";
    static final String CIAN = "\u001B[36m";
    static final String MORADO = "\u001B[35m";
    static final String ROJO = "\u001B[31m";
    static final String AMARILLO = "\u001B[33m";

    static final String BIENVENIDA = "BIENVENIDO AL SERVIDOR LOCAL";

    static final List<ClienteHandler> clientes = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        // Código único otorgado por el servidor del profesor: XXXX-XXXX

        BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));
        System.out.print(AMARILLO + "Ingrese puerto del servidor: " + RESET);
        int puerto = Integer.parseInt(teclado.readLine().trim());

        try (ServerSocket serverSocket = new ServerSocket(puerto)) {
            System.out.println(AMARILLO + "[SISTEMA] Servidor escuchando en puerto " + puerto + RESET);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println(AMARILLO + "[SISTEMA] Cliente conectado: " + socket.getInetAddress() + RESET);
                ClienteHandler handler = new ClienteHandler(socket);
                synchronized (clientes) {
                    clientes.add(handler);
                }
                new Thread(handler).start();
            }
        } catch (IOException e) {
            System.out.println(ROJO + "[ERROR] " + e.getMessage() + RESET);
        }
    }

    static void difundir(String mensaje) {
        synchronized (clientes) {
            for (ClienteHandler c : clientes) {
                c.enviar(mensaje);
            }
        }
    }

    static class ClienteHandler implements Runnable {
        private final Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        ClienteHandler(Socket socket) {
            this.socket = socket;
        }

        void enviar(String mensaje) {
            out.println(mensaje);
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                enviar(BIENVENIDA);

                String linea;
                while ((linea = in.readLine()) != null) {
                    linea = linea.trim();
                    if (linea.isEmpty()) continue;

                    if (linea.equals("/hi")) {
                        enviar(BIENVENIDA);
                    } else if (linea.equals("/logout")) {
                        break;
                    } else if (linea.startsWith("/")) {
                        enviar("COMANDO DESCONOCIDO");
                    } else {
                        System.out.println(MORADO + "[CHAT] " + linea + RESET);
                        difundir(linea);
                    }
                }
            } catch (IOException e) {
            } finally {
                synchronized (clientes) {
                    clientes.remove(this);
                }
                try {
                    socket.close();
                } catch (IOException ignored) {
                }
                System.out.println(AMARILLO + "[SISTEMA] Cliente desconectado" + RESET);
            }
        }
    }
}
