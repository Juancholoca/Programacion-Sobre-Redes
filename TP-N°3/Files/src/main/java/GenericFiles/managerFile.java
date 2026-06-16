package GenericFiles;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class managerFile {

	private File file;

	public managerFile(String nombreArchivo) {
		file = new File(nombreArchivo);

		try {
			if (!file.exists()) {
				file.createNewFile();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void agregarProducto(dtoProductos producto) {

		try {
			FileWriter fw = new FileWriter(file, true);
			PrintWriter pw = new PrintWriter(fw);

			pw.println(producto.toString());

			pw.close();
			fw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<dtoProductos> leerProductos() {

		ArrayList<dtoProductos> lista = new ArrayList<>();

		try {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);

			String linea;

			while ((linea = br.readLine()) != null) {

				String[] datos = linea.split(";");

				if (datos.length == 4) {

					dtoProductos producto = new dtoProductos(
							datos[0],
							Float.parseFloat(datos[1]),
							Float.parseFloat(datos[2]),
							Integer.parseInt(datos[3]));

					lista.add(producto);
				}
			}

			br.close();
			fr.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return lista;
	}

	public void mostrarProductos() {

		ArrayList<dtoProductos> lista = leerProductos();

		System.out.println();
		System.out.println("==============================================================");
		System.out.printf("%-20s %-15s %-15s %-10s%n",
				"PRODUCTO", "P.COMPRA", "P.VENTA", "STOCK");
		System.out.println("==============================================================");

		for (dtoProductos p : lista) {

			System.out.printf("%-20s %-15.2f %-15.2f %-10d%n",
					p.getNombre(),
					p.getPrecioCompra(),
					p.getPrecioVenta(),
					p.getStock());
		}

		System.out.println("==============================================================");
	}

	public void editarProducto(String nombreBuscado, dtoProductos nuevoProducto) {

		ArrayList<dtoProductos> lista = leerProductos();

		for (int i = 0; i < lista.size(); i++) {

			if (lista.get(i).getNombre().equalsIgnoreCase(nombreBuscado)) {

				lista.set(i, nuevoProducto);
				break;
			}
		}

		guardarLista(lista);
	}

	public void eliminarProducto(String nombreBuscado) {

		ArrayList<dtoProductos> lista = leerProductos();

		lista.removeIf(
				p -> p.getNombre().equalsIgnoreCase(nombreBuscado));

		guardarLista(lista);
	}

	private void guardarLista(ArrayList<dtoProductos> lista) {

		try {

			FileWriter fw = new FileWriter(file);
			PrintWriter pw = new PrintWriter(fw);

			for (dtoProductos p : lista) {
				pw.println(p.toString());
			}

			pw.close();
			fw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}