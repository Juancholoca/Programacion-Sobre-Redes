package TPN5;

import java.io.*;
import java.util.*;

public class Gestor {
	
    public static void guardarBinario(List<Servidor> lista) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("nodos.dat"))) {
            oos.writeObject(lista);
        }
    }

    @SuppressWarnings("unchecked")
    public static List<Servidor> cargarBinario() throws Exception {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("nodos.dat"))) {
            return (List<Servidor>) ois.readObject();
        }
    }

    public static void exportarJSON(List<Servidor> lista) throws IOException {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("config_red.json")))) {
            pw.println("[");
            for (int i = 0; i < lista.size(); i++) {
                Servidor s = lista.get(i);
                pw.printf("  {\"ip\":\"%s\", \"nombre\":\"%s\", \"endpoint\":\"%s\"}%s\n", 
                          s.ip, s.nombre, s.endpoint, (i < lista.size() - 1 ? "," : ""));
            }
            pw.println("]");
        }
    }

    public static List<Servidor> importarJSON() throws IOException {
        List<Servidor> lista = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("config_red.json"))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.contains("\"ip\"")) {
                    String[] partes = linea.split("\"");
                    lista.add(new Servidor(partes[3], partes[7], partes[11]));
                }
            }
        }
        return lista;
    }
}