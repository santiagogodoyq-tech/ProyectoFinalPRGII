package co.edu.uniquindio.poo.proyectofinalprgii.Class.Comportamental.Observer;

import co.edu.uniquindio.poo.proyectofinalprgii.Class.ServicioMetricas;

public class ObserverMetrica implements ObserverEstado {
    private final ServicioMetricas servicioMetricas;

    public ObserverMetrica(ServicioMetricas servicioMetricas) {
        this.servicioMetricas = servicioMetricas;
    }

    @Override
    public void actualizar(String entidad, String id, String estadoAnterior, String estadoNuevo) {
        System.out.printf("[MÉTRICAS] %s #%s: %s → %s%n", entidad, id, estadoAnterior, estadoNuevo);
        servicioMetricas.registrarEvento(entidad, id, estadoAnterior, estadoNuevo);
    }
}
