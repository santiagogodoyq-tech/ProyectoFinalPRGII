package co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas;

import co.edu.uniquindio.poo.proyectofinalprgii.Class.Comportamental.Observer.ObserverEstado;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.Enums.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Evento {

    private String idEvento;
    private String nombre;
    private String categoria;
    private String descripcion;
    private String ciudad;
    private LocalDateTime fecha;
    private String politicas;
    private EstadoEvento estado;
    private Recinto recinto;
    private ArrayList<ObserverEstado> observadores;

    public Evento(String idEvento, String nombre, String categoria, String descripcion,
                  String ciudad, LocalDateTime fecha, String politicas, Recinto recinto) {
        this.idEvento = idEvento;
        this.nombre = nombre;
        this.categoria = categoria;
        this.descripcion = descripcion;
        this.ciudad = ciudad;
        this.fecha = fecha;
        this.politicas = politicas;
        this.recinto = recinto;
        this.estado = EstadoEvento.BORRADOR;
        this.observadores = new ArrayList<>();
    }

    public void agregarObservador(ObserverEstado obs) {
        observadores.add(obs);
    }

    private void notificarObservadores(EstadoEvento anterior, EstadoEvento nuevo) {
        for (ObserverEstado obs : observadores) {
            obs.actualizar("Evento", idEvento, anterior.name(), nuevo.name());
        }
    }

    public void publicar() {
        EstadoEvento anterior = this.estado;
        this.estado = EstadoEvento.PUBLICADO;
        notificarObservadores(anterior, this.estado);
    }

    public void pausar() {
        EstadoEvento anterior = this.estado;
        this.estado = EstadoEvento.PAUSADO;
        notificarObservadores(anterior, this.estado);
    }

    public void cancelar() {
        EstadoEvento anterior = this.estado;
        this.estado = EstadoEvento.CANCELADO;
        notificarObservadores(anterior, this.estado);
    }

    public String getIdEvento()     { return idEvento; }
    public String getNombre()       { return nombre; }
    public String getCategoria()    { return categoria; }
    public String getDescripcion()  { return descripcion; }
    public String getCiudad()       { return ciudad; }
    public LocalDateTime getFecha() { return fecha; }
    public String getPoliticas()    { return politicas; }
    public EstadoEvento getEstado() { return estado; }
    public Recinto getRecinto()     { return recinto; }
    public void setNombre(String nombre)           { this.nombre = nombre; }
    public void setCategoria(String categoria)     { this.categoria = categoria; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setCiudad(String ciudad)           { this.ciudad = ciudad; }
    public void setFecha(LocalDateTime fecha)      { this.fecha = fecha; }
    public void setPoliticas(String politicas)     { this.politicas = politicas; }
}
