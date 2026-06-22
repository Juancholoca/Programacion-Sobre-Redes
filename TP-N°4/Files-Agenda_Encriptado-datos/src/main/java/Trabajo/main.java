package Trabajo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class main {

private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

public static final String RESET = "\u001B[0m";
public static final String ROJO = "\u001B[31m";
public static final String VERDE = "\u001B[32m";
public static final String AMARILLO = "\u001B[33m";
public static final String CYAN = "\u001B[36m";

public static void main(String[] args) {

	managerAgenda agenda = new managerAgenda("agenda.dat");

	int opcion = 0;

	while (opcion != 5) {

		mostrarMenu();

		opcion = convertirInt(leerTexto("Seleccione una opcion: "));

		switch (opcion) {

		case 1:
			agregarContacto(agenda);
			break;

		case 2:
			agenda.mostrar();
			break;

		case 3:
			editarContacto(agenda);
			break;

		case 4:
			eliminarContacto(agenda);
			break;

		case 5:
			System.out.println(VERDE +"\nSistema finalizado." +	RESET);
			break;

		default:
			System.out.println(ROJO +"\nOpcion incorrecta." + RESET);
		}
	}
}

private static void mostrarMenu() {

	System.out.println();

	System.out.println(CYAN +"========================================");

	System.out.println("AGENDA AES");

	System.out.println("========================================");

	System.out.println("1 - Agregar Contacto");

	System.out.println("2 - Mostrar Contactos");

	System.out.println("3 - Editar Contacto");

	System.out.println("4 - Eliminar Contacto");

	System.out.println("5 - Salir");

	System.out.println("========================================"+ RESET);
}

private static void agregarContacto(managerAgenda agenda) {

	String nombre =	leerTexto("Nombre: ");

	String telefono = leerTexto("Telefono: ");

	String email = leerTexto("Email: ");

	String nota = leerTexto("Nota Privada: ");

	dtoContacto contacto = new dtoContacto(nombre,telefono,email,nota);

	agenda.agregar(contacto);

	System.out.println(VERDE + "\nContacto agregado correctamente." + RESET);
}

private static void editarContacto(managerAgenda agenda) {

	String buscar = leerTexto("Nombre del contacto a editar: ");

	String nombre =	leerTexto("Nuevo nombre: ");

	String telefono = leerTexto("Nuevo telefono: ");

	String email = leerTexto("Nuevo email: ");

	String nota = leerTexto("Nueva nota privada: ");

	dtoContacto contacto = new dtoContacto(nombre,telefono,email,nota);

	agenda.editar(buscar, contacto);

	System.out.println(AMARILLO + "\nContacto editado correctamente." + RESET);
}

private static void eliminarContacto(managerAgenda agenda) {

	String nombre =	leerTexto("Nombre del contacto a eliminar: ");

	agenda.eliminar(nombre);

	System.out.println(ROJO + "\nContacto eliminado." + RESET);
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

public static int convertirInt(String texto) {

	while (!esEntero(texto)) {

		texto =	leerTexto("Ingrese un numero valido: ");
	}

	return Integer.parseInt(texto);
}

}
