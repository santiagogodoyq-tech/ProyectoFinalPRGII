package co.edu.uniquindio.poo.proyectofinalprgii.Class.Comportamental.Observer;

public class ObserverIncidencia implements ObserverEstado {
    @Override
    public void actualizar(String entidad, String id, String estadoAnterior, String estadoNuevo) {
        if ("INCIDENCIA".equals(estadoNuevo) || "CANCELADO".equals(estadoNuevo)) {
            System.out.printf("[INCIDENCIA DETECTADA] %s #%s pasó a %s%n", entidad, id, estadoNuevo);
        }
    }
}
