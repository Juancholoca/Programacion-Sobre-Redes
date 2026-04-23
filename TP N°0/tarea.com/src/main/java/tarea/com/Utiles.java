package tarea.com;

import java.io.*;

public class Utiles {

    private static BufferedReader lector;
    private static PrintWriter out;

    public Utiles() {
        lector = new BufferedReader(new InputStreamReader(System.in));
        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out)), true);
    }

    public static BufferedReader getLector() {
        return lector;
    }

    public static PrintWriter getOut() {
        return out;
    }
}