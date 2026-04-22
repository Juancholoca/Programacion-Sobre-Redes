package tarea.com;

public class MenuEjercicios {

    public MenuEjercicios() {

        var lector = Utiles.getLector();
        var out = Utiles.getOut();

        ej1 e1 = new ej1();
        ej2 e2 = new ej2();
        ej3 e3 = new ej3();
        ej4 e4 = new ej4();
        ej5 e5 = new ej5();
        ej6 e6 = new ej6();
        ej7 e7 = new ej7();
        ej8 e8 = new ej8();
        ej9 e9 = new ej9();
        ej10 e10 = new ej10();
        ej11 e11 = new ej11();
        ej12 e12 = new ej12();
        ej13 e13 = new ej13();
        ej14 e14 = new ej14();
        ej15 e15 = new ej15();

        boolean continuar = true;

        while (continuar) {
            try {
                out.println("\n===== MENU =====");
                out.println("1  Ej1");
                out.println("2  Ej2");
                out.println("3  Ej3");
                out.println("4  Ej4");
                out.println("5  Ej5");
                out.println("6  Ej6");
                out.println("7  Ej7");
                out.println("8  Ej8");
                out.println("9  Ej9");
                out.println("10 Ej10");
                out.println("11 Ej11");
                out.println("12 Ej12");
                out.println("13 Ej13");
                out.println("14 Ej14");
                out.println("15 Ej15");
                out.println("0  Salir");

                int op = Integer.parseInt(lector.readLine());

                switch (op) {
                    case 1: e1.ejecutar(); break;
                    case 2: e2.ejecutar(); break;
                    case 3: e3.ejecutar(); break;
                    case 4: e4.ejecutar(); break;
                    case 5: e5.ejecutar(); break;
                    case 6: e6.ejecutar(); break;
                    case 7: e7.ejecutar(); break;
                    case 8: e8.ejecutar(); break;
                    case 9: e9.ejecutar(); break;
                    case 10: e10.ejecutar(); break;
                    case 11: e11.ejecutar(); break;
                    case 12: e12.ejecutar(); break;
                    case 13: e13.ejecutar(); break;
                    case 14: e14.ejecutar(); break;
                    case 15: e15.ejecutar(); break;
                    case 0: continuar = false; break;
                    default: out.println("Opcion invalida");
                }

            } catch (Exception e) {
                out.println("Error");
            }
        }
    }
}