package co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas;

import co.edu.uniquindio.poo.proyectofinalprgii.Class.Enums.EstadoEvento;

import java.time.LocalDateTime;

public class Incidencia {

    private String idIncidencia;
    private String tipo;
    private String descripcion;
    private LocalDateTime fecha;
    private String entidadAfectada;
    private Evento evento;
    private Compra compra;

    public Incidencia(String idIncidencia, String tipo, String descripcion,
                      LocalDateTime fecha, String entidadAfectada, Evento evento, Compra compra) {
        this.idIncidencia = idIncidencia;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.entidadAfectada = entidadAfectada;
        this.evento = evento;
        this.compra = compra;
    }

    public String getIdIncidencia()    { return idIncidencia; }
    public String getTipo()            { return tipo; }
    public String getDescripcion()     { return descripcion; }
    public LocalDateTime getFecha()    { return fecha; }
    public String getEntidadAfectada() { return entidadAfectada; }
    public Evento getEvento()          { return evento; }
    public Compra getCompra()          { return compra; }
}
