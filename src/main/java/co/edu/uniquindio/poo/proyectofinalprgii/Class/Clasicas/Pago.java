package co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas;


import co.edu.uniquindio.poo.proyectofinalprgii.Class.MetodoPago;

import java.time.LocalDateTime;

public class Pago {

    private String idPago;
    private double monto;
    private LocalDateTime fecha;
    private String referencia;
    private MetodoPago metodoPago;

    public Pago(String idPago, double monto, LocalDateTime fecha, String referencia, MetodoPago metodoPago) {
        this.idPago = idPago;
        this.monto = monto;
        this.fecha = fecha;
        this.referencia = referencia;
        this.metodoPago = metodoPago;
    }

    public void confirmar() {
        System.out.println("Pago " + idPago + " confirmado. Referencia: " + referencia);
    }

    public void anular() {
        System.out.println("Pago " + idPago + " anulado.");
    }

    public String getIdPago()         { return idPago; }
    public double getMonto()          { return monto; }
    public LocalDateTime getFecha()   { return fecha; }
    public String getReferencia()     { return referencia; }
    public MetodoPago getMetodoPago() { return metodoPago; }
}