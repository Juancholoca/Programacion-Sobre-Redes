package hilos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Cliente {

    static final String RESET = "\u001B[0m";
    static final String VERDE = "\u001B[32m";
    static final String CIAN = "\u001B[36m";
    static final String MORADO = "\u001B[35m";
    static final String ROJO = "\u001B[31m";
    static final String AMARILLO = "\u001B[33m";

   static volatile boolean modoCifrado = false;
    static volatile boolean activo = true;

    static void enviarCifrado (PrintWriter out, String mensaje) {
    	try {
    		out.println(Cifrado.encriptar(mensaje));
    	} catch (Exception e) {
    		
    	}
    }
    
    public static void main(String[] args) throws IOException {
      
        BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));
        System.out.print(AMARILLO + "Ingrese IP: " + RESET);
        String ip = teclado.readLine().trim();
        System.out.print(AMARILLO + "Ingrese puerto: " + RESET);
        int puerto = Integer.parseInt(teclado.readLine().trim());

        try {
            Socket socket = new Socket(ip, puerto);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            System.out.println(AMARILLO + "\n[SISTEMA] Conectado a " + ip + ":" + puerto
                    + ". Comandos: /hi, /logout\n" + RESET);

           Thread escucha = new Thread(() -> {
                try {
                    String linea;
                    while ((linea = in.readLine()) != null) {
                        String texto = Cifrado.desencriptarONull(linea);
                        if (texto != null) {
                            modoCifrado = true;
                        } else {
                            texto = linea;
                                 
                        }
                        
                        texto = texto.trim();
                        
                        if (texto.equals("/PING_CHECK")) {
                        	enviarCifrado(out,"/PONG_ALIVE");
                            continue;
                        	
                        }
                        
                        if (texto.startsWith("/DESAFIO_CIFRADO ")) {
                        	System.out.println(MORADO + "\t[DESAFIO]" + texto + RESET);
                        	
                        	String[] partes = texto.split(" ", 3);
                        	if (partes.length == 3) {
                        		try {
                        			String resuelto = Cifrado.encriptar(partes[2], partes[1]);
                        			String respuesta = "/RESPUESTA_CIFRADO " + resuelto;
                        			System.out.println(MORADO + "\t[RESPUESTA]" + respuesta + RESET);
                        			
                        			enviarCifrado(out, respuesta);
                        		
                        		} catch(Exception e) {
                        			System.out.println(ROJO + "\t[ERROR DESAFIO]" + e + RESET);
                        		}
                        	}
                        	continue;
                       	
                        }
                       
                        System.out.println(CIAN + "\t[SERVIDOR] " + texto + RESET);
                    }
                } catch (IOException e) {
               
                } finally {
                    activo = false;
                    System.out.println(AMARILLO + "\n[SISTEMA] Conexión cerrada. Presione Enter para salir." + RESET);
                }
            });
            escucha.start();

            String entrada;
            while (activo && (entrada = teclado.readLine()) != null) {
                entrada = entrada.trim();
                if (!activo) break;
                if (entrada.isEmpty()) continue;

                System.out.println(VERDE + "\t[YO] " + entrada + RESET);

                String aEnviar = entrada;
                if (modoCifrado) {
                    try {
                        aEnviar = Cifrado.encriptar(entrada);
                    } catch (Exception e) {
                        aEnviar = entrada;
                    }
                }
                out.println(aEnviar);

                if (entrada.equals("/logout")) {
                    activo = false;
                    break;
                }
            }

            socket.close();
            System.out.println(AMARILLO + "[SISTEMA] Desconectado." + RESET);

        } catch (IOException e) {
            System.out.println(ROJO + "[ERROR] No se pudo conectar: " + e.getMessage() + RESET);
        }
    }
}
