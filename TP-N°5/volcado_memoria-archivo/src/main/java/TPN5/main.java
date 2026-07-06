package TPN5;

import java.util.*;

public class main {
    public static void main(String[] args) {
        List<Servidor> lista = new ArrayList<>();
        Scanner sc = new Scanner(System.in);
        Random r = new Random();
        boolean ejecutando = true;

        while (ejecutando) {
            System.out.println("\n--- MENU DE GESTION DE SERVIDORES ---");
            System.out.println("1. Cargar Nodos (Binario)");
            System.out.println("2. ABMC Servidores (Alta/Baja/Mod)");
            System.out.println("3. Simular Chequeo");
            System.out.println("4. Guardar Estado (Binario)");
            System.out.println("5. Exportar JSON");
            System.out.println("6. Importar JSON");
            System.out.println("7. Mostrar Monitor Ordenado");
            System.out.println("0. Salir");
            System.out.print("Seleccione opcion: ");
            
            int op = sc.nextInt();
            sc.nextLine();

            try {
                switch (op) {
                    case 1: 
                        lista = Gestor.cargarBinario(); 
                        System.out.println("Binario cargado correctamente.");
                        break;
                    case 2: 
                        System.out.println("1. Alta | 2. Baja | 3. Modificar");
                        int subOp = sc.nextInt(); sc.nextLine();
                        if (subOp == 1) {
                            System.out.print("IP: "); String ip = sc.nextLine();
                            System.out.print("Nombre: "); String nom = sc.nextLine();
                            System.out.print("Endpoint: "); String end = sc.nextLine();
                            lista.add(new Servidor(ip, nom, end));
                        } else if (subOp == 2) {
                            System.out.print("Ingrese IP a eliminar: "); String ipB = sc.nextLine();
                            lista.removeIf(s -> s.ip.equals(ipB));
                        } else if (subOp == 3) {
                            System.out.print("Ingrese IP a modificar: "); String ipM = sc.nextLine();
                            for (Servidor s : lista) {
                                if (s.ip.equals(ipM)) {
                                    System.out.print("Nuevo Nombre: "); s.nombre = sc.nextLine();
                                }
                            }
                        }
                        break;
                    case 3: 
                        for(Servidor s : lista) { 
                            int[] estados = {200, 404, 500};
                            s.estadoHttp = estados[r.nextInt(3)];
                            s.tiempoRespuesta = r.nextInt(1000); 
                        }
                        System.out.println("Simulación completada.");
                        break;
                    case 4: 
                        Gestor.guardarBinario(lista);
                        System.out.println("Estado guardado en nodos.dat");
                        break;
                    case 5: 
                        Gestor.exportarJSON(lista);
                        System.out.println("config_red.json generado.");
                        break;
                    case 6:
                        lista = Gestor.importarJSON();
                        System.out.println("Configuración JSON importada.");
                        break;
                    case 7: 
                        System.out.println("\nIP             | Nombre          | Estado | Tiempo");
                        System.out.println("---------------------------------------------------");
                       
                        lista.sort((a,b) -> Integer.compare(b.estadoHttp, a.estadoHttp));
                        for(Servidor s : lista) System.out.println(s);
                        break;
                    case 0: ejecutando = false; break;
                    default: System.out.println("Opcion no valida.");
                }
            } catch (Exception e) { 
                System.out.println("Error en la operacion: " + e.getMessage()); 
            }
        }
        sc.close();
    }
}