package GenericFiles;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class main {

	private static BufferedReader br =
			new BufferedReader(new InputStreamReader(System.in));

	public static void main(String[] args) {

		managerFile manager = new managerFile("Inventario.dat");

		int opcion = 0;

		while (opcion != 5) {

			mostrarMenu();

			opcion = convertirInt(leerTexto("Seleccione una opcion: "));

			switch (opcion) {

			case 1:
				agregar(manager);
				break;

			case 2:
				manager.mostrarProductos();
				break;

			case 3:
				editar(manager);
				break;

			case 4:
				eliminar(manager);
				break;

			case 5:
				System.out.println("\nSaliendo del sistema...");
				break;

			default:
				System.out.println("\nOpcion invalida.");
			}
		}
	}

	private static void mostrarMenu() {

		System.out.println();
		System.out.println("======================================");
		System.out.println("      SISTEMA DE INVENTARIO");
		System.out.println("======================================");
		System.out.println("1 - Agregar Producto");
		System.out.println("2 - Mostrar Productos");
		System.out.println("3 - Editar Producto");
		System.out.println("4 - Eliminar Producto");
		System.out.println("5 - Salir");
		System.out.println("======================================");
	}

	private static void agregar(managerFile manager) {

		String nombre = leerTexto("Nombre: ");

		float compra =
				convertirFloat(leerTexto("Precio de compra: "));

		float venta =
				convertirFloat(leerTexto("Precio de venta: "));

		int stock =
				convertirInt(leerTexto("Stock: "));

		dtoProductos producto =
				new dtoProductos(nombre, compra, venta, stock);

		manager.agregarProducto(producto);

		System.out.println("\nProducto agregado correctamente.");
	}

	private static void editar(managerFile manager) {

		String buscar =
				leerTexto("Producto a editar: ");

		String nombre =
				leerTexto("Nuevo nombre: ");

		float compra =
				convertirFloat(leerTexto("Nuevo precio compra: "));

		float venta =
				convertirFloat(leerTexto("Nuevo precio venta: "));

		int stock =
				convertirInt(leerTexto("Nuevo stock: "));

		dtoProductos nuevo =
				new dtoProductos(nombre, compra, venta, stock);

		manager.editarProducto(buscar, nuevo);

		System.out.println("\nProducto editado.");
	}

	private static void eliminar(managerFile manager) {

		String nombre =
				leerTexto("Producto a eliminar: ");

		manager.eliminarProducto(nombre);

		System.out.println("\nProducto eliminado.");
	}

	public static String leerTexto(String mensaje) {

		try {

			System.out.print(mensaje);
			return br.readLine();

		} catch (IOException e) {

			return "";
		}
	}

	public static boolean esEntero(String texto) {

		try {

			Integer.parseInt(texto);
			return true;

		} catch (Exception e) {

			return false;
		}
	}

	public static boolean esDecimal(String texto) {

		try {

			Float.parseFloat(texto);
			return true;

		} catch (Exception e) {

			return false;
		}
	}

	public static int convertirInt(String texto) {

		while (!esEntero(texto)) {

			texto = leerTexto("Ingrese un entero valido: ");
		}

		return Integer.parseInt(texto);
	}

	public static float convertirFloat(String texto) {

		while (!esDecimal(texto)) {

			texto = leerTexto("Ingrese un numero valido: ");
		}

		return Float.parseFloat(texto);
	}
}