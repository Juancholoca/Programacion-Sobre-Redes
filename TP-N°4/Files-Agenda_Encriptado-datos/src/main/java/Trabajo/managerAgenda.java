package Trabajo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class managerAgenda {

private File archivo;
private ArrayList<dtoContacto> contactos;

public managerAgenda(String nombreArchivo) {

	archivo = new File(nombreArchivo);
	contactos = new ArrayList<>();

	try {

		if (!archivo.exists()) {
			archivo.createNewFile();
		}

		cargarDatos();

	} catch (IOException e) {
		e.printStackTrace();
	}
}

private void cargarDatos() {

	try {

		FileReader fr = new FileReader(archivo);
		BufferedReader br = new BufferedReader(fr);

		String linea;

		while ((linea = br.readLine()) != null) {

			String[] datos = linea.split(";");

			if (datos.length == 4) {

				dtoContacto contacto = new dtoContacto(datos[0],datos[1],datos[2],datos[3]);

				contactos.add(contacto);
			}
		}

		br.close();
		fr.close();

	} catch (IOException e) {
		e.printStackTrace();
	}
}

public void agregar(dtoContacto contacto) {

	contactos.add(contacto);
	guardarDatos();
}

public void eliminar(String nombre) {

	contactos.removeIf(c -> c.getNombre().equalsIgnoreCase(nombre));

	guardarDatos();
}

public void editar(String nombreBuscado,dtoContacto nuevoContacto) {

	for (int i = 0; i < contactos.size(); i++) {

		if (contactos.get(i).getNombre().equalsIgnoreCase(nombreBuscado)) {

			contactos.set(i, nuevoContacto);
			break;
		}
	}

	guardarDatos();
}

public void mostrar() {

	System.out.println();
	System.out.println("\u001B[36m==============================================================");

	System.out.printf("%-20s %-15s %-25s %-20s%n","NOMBRE","TELEFONO","EMAIL","NOTA PRIVADA");

	System.out.println("==============================================================\u001B[0m");

	for (dtoContacto c : contactos) {

		String nota = EncriptUtil.desencriptar(c.getNotaPrivada());

		System.out.printf("%-20s %-15s %-25s %-20s%n",c.getNombre(),c.getTelefono(),c.getEmail(),nota);
	}
}

private void guardarDatos() {

	File temporal = new File("agenda.tmp");

	try {

		FileWriter fw =	new FileWriter(temporal);

		PrintWriter pw = new PrintWriter(fw);

		for (dtoContacto c : contactos) {

			String notaEncriptada =	EncriptUtil.encriptar(c.getNotaPrivada());

			pw.println(c.getNombre() + ";" + c.getTelefono() + ";" + c.getEmail() + ";" + notaEncriptada);
		}

		pw.close();
		fw.close();

		if (archivo.exists()) {
			archivo.delete();
		}

		temporal.renameTo(archivo);

	} catch (IOException e) {

		e.printStackTrace();
	}
}

}
