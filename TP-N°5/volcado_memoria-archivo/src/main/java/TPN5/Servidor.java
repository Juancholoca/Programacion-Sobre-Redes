package TPN5;

import java.io.Serializable;

public class Servidor implements Serializable {
    private static final long serialVersionUID = 1L;
    public String ip, nombre, endpoint;
    public int estadoHttp;
    public long tiempoRespuesta;

    public Servidor(String ip, String nombre, String endpoint) {
        this.ip = ip;
        this.nombre = nombre;
        this.endpoint = endpoint;
        this.estadoHttp = 0;
        this.tiempoRespuesta = 0;
    }

    @Override
    public String toString() {
        String color = (estadoHttp == 200) ? "\u001B[32m" : 
                       (estadoHttp >= 500 || tiempoRespuesta > 500) ? "\u001B[31;1m" : "\u001B[33m";
        return String.format(color + "%-15s | %-15s | %-6d | %dms" + "\u001B[0m", 
                             ip, nombre, estadoHttp, tiempoRespuesta);
    }
}