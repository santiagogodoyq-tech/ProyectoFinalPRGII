package co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas;

import co.edu.uniquindio.poo.proyectofinalprgii.Class.Enums.EstadoEntrada;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.PrecioEntrada;

import java.util.UUID;

public class Entrada {
    private String idEntrada;
    private double precioFinal;
    private EstadoEntrada estado;
    private Zona zona;
    private Asiento asiento;
    private PrecioEntrada precioEntrada;

    public Entrada(String idEntrada, Zona zona, Asiento asiento, PrecioEntrada precioEntrada) {
        this.idEntrada = idEntrada;
        this.zona = zona;
        this.asiento = asiento;
        this.precioEntrada = precioEntrada;
        this.precioFinal = precioEntrada.calcularTotal();
        this.estado = EstadoEntrada.ACTIVA;
    }

    public String generarQR() {
        String qr = "QR-" + idEntrada + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        System.out.println("QR generado: " + qr);
        return qr;
    }

    public void anular() {
        this.estado = EstadoEntrada.ANULADA;
        System.out.println("Entrada " + idEntrada + " anulada.");
    }

    public String getIdEntrada()          { return idEntrada; }
    public double getPrecioFinal()        { return precioFinal; }
    public EstadoEntrada getEstado()      { return estado; }
    public Zona getZona()                 { return zona; }
    public Asiento getAsiento()           { return asiento; }
    public PrecioEntrada getPrecioEntrada() { return precioEntrada; }

    public void setEstado(EstadoEntrada estado) { this.estado = estado; }
}
