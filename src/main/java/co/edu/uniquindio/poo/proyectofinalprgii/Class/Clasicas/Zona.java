package co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas;

import co.edu.uniquindio.poo.proyectofinalprgii.Class.Enums.EstadoAsiento;

import java.util.ArrayList;

public class Zona {

    private String idZona;
    private String nombre;
    private int capacidad;
    private ArrayList<Asiento> asientos;

    public Zona(String idZona, String nombre, int capacidad) {
        this.idZona = idZona;
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.asientos = new ArrayList<>();
    }

    public void agregarAsiento(Asiento asiento) {
        asientos.add(asiento);
    }

    public int consultarOcupacion() {
        return (int) asientos.stream()
                .filter(a -> a.getEstado() == EstadoAsiento.VENDIDO || a.getEstado() == EstadoAsiento.RESERVADO)
                .count();
    }

    public int consultarDisponibilidad() {
        return (int) asientos.stream()
                .filter(a -> a.getEstado() == EstadoAsiento.DISPONIBLE)
                .count();
    }

    public String getIdZona()    { return idZona; }
    public String getNombre()    { return nombre; }
    public int getCapacidad()    { return capacidad; }
    public ArrayList<Asiento> getAsientos() { return asientos; }
}
