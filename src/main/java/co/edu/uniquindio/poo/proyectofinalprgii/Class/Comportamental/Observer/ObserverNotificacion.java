package co.edu.uniquindio.poo.proyectofinalprgii.Class.Comportamental.Observer;

import co.edu.uniquindio.poo.proyectofinalprgii.Class.ServicioCorreo;
import co.edu.uniquindio.poo.proyectofinalprgii.UI.DataStore;

public class ObserverNotificacion implements ObserverEstado {
    @Override
    // PONER:
    public void actualizar(String entidad, String id,
                           String estadoAnterior, String estadoNuevo) {
        System.out.printf("[NOTIFICACIÓN] %s #%s cambió de %s → %s%n",
                entidad, id, estadoAnterior, estadoNuevo);

        if (!"Evento".equals(entidad)) return;
        if (DataStore.instance == null) return; // evitar recursión durante seed

        DataStore ds = DataStore.instance;
        ds.eventos.stream()
                .filter(e -> e.getIdEvento().equals(id))
                .findFirst()
                .ifPresent(evento -> {
                    var comprasAfectadas = ds.compras.stream()
                            .filter(c -> c.getEvento().getIdEvento().equals(id))
                            .toList();
                    switch (estadoNuevo) {
                        case "CANCELADO" ->
                                ServicioCorreo.notificarCancelacionEvento(evento, comprasAfectadas);
                        case "PAUSADO" ->
                                ServicioCorreo.notificarCambioEstadoEvento(evento, comprasAfectadas,
                                        "⏸ Evento pausado", "El evento ha sido pausado temporalmente.");
                        case "PUBLICADO" ->
                                ServicioCorreo.notificarCambioEstadoEvento(evento, comprasAfectadas,
                                        "▶ Evento publicado", "El evento está activo nuevamente.");
                    }
                });
    }
}
