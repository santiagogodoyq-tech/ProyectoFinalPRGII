package co.edu.uniquindio.poo.proyectofinalprgii.Class.Creacional.Singleton;

import co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas.Asiento;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas.Zona;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.Enums.EstadoAsiento;

import java.util.HashMap;
import java.util.Map;

public class GestorDisponibilidad {
    private static GestorDisponibilidad instancia;
    private final Map<String, Asiento> mapaAsientos;

    private GestorDisponibilidad() {
        this.mapaAsientos = new HashMap<>();
    }

    public static synchronized GestorDisponibilidad getInstancia() {
        if (instancia == null) {
            instancia = new GestorDisponibilidad();
        }
        return instancia;
    }

    public void registrarAsiento(Asiento asiento) {
        mapaAsientos.put(asiento.getIdAsiento(), asiento);
    }

    public boolean reservar(String idAsiento) {
        Asiento asiento = mapaAsientos.get(idAsiento);
        if (asiento != null && asiento.getEstado() == EstadoAsiento.DISPONIBLE) {
            asiento.cambiarEstado(EstadoAsiento.RESERVADO);
            System.out.println("Asiento " + idAsiento + " reservado.");
            return true;
        }
        System.out.println("Asiento " + idAsiento + " no disponible.");
        return false;
    }

    public void liberar(String idAsiento) {
        Asiento asiento = mapaAsientos.get(idAsiento);
        if (asiento != null) {
            asiento.cambiarEstado(EstadoAsiento.DISPONIBLE);
            System.out.println("Asiento " + idAsiento + " liberado.");
        }
    }

    public int consultarOcupacion(Zona zona) {
        return zona.consultarOcupacion();
    }
}
