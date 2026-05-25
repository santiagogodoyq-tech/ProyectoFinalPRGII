package co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas;

import co.edu.uniquindio.poo.proyectofinalprgii.Class.Enums.EstadoAsiento;

public class Asiento {

    private String idAsiento;
    private String fila;
    private int numero;
    private EstadoAsiento estado;
    private String sector;   // ORIENTAL, CENTRAL, OCCIDENTAL
    private String tipoZona;
    private double precioPersonalizado = -1;

    public Asiento(String idAsiento, String fila, int numero,
                   String sector, String tipoZona) {
        this.idAsiento = idAsiento;
        this.fila      = fila;
        this.numero    = numero;
        this.sector    = sector;
        this.tipoZona  = tipoZona;
        this.estado    = EstadoAsiento.DISPONIBLE;
    }

    // Constructor de compatibilidad hacia atrás
    public Asiento(String idAsiento, String fila, int numero) {
        this(idAsiento, fila, numero, "CENTRAL", "GENERAL");
    }

    public void cambiarEstado(EstadoAsiento nuevoEstado) { this.estado = nuevoEstado; }

    public String getIdAsiento()  { return idAsiento; }
    public String getFila()       { return fila; }
    public int getNumero()        { return numero; }
    public EstadoAsiento getEstado() { return estado; }
    public String getSector()     { return sector; }
    public String getTipoZona()   { return tipoZona; }
    public void setPrecio(double precio) { this.precioPersonalizado = precio; }

    public double getPrecioSegunTipo() {
        if (precioPersonalizado >= 0) return precioPersonalizado;
        return switch (tipoZona) {
            case "VIP"          -> 246_000;
            case "PREFERENCIAL" -> 124_000;
            default             -> 62_000;
        };
    } // GENERAL
        };


