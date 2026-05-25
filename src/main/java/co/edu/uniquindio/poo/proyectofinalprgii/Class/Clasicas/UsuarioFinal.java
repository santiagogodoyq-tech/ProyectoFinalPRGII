package co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas;

import co.edu.uniquindio.poo.proyectofinalprgii.Class.MetodoPago;
import java.util.ArrayList;

public class UsuarioFinal extends Usuario {
    private ArrayList<MetodoPago> metodosPago;

    public UsuarioFinal(String idUsuario, String nombre,
                        String correo, String telefono, String contrasena) {
        super(idUsuario, nombre, correo, telefono, contrasena);
        this.metodosPago = new ArrayList<>();
    }

    @Override public void iniciarSesion() {
        System.out.println("UsuarioFinal " + nombre + " inició sesión.");
    }

    @Override public String getRol() { return "USUARIO"; }

    public ArrayList<Evento> explorarEventos()    { return new ArrayList<>(); }
    public Compra crearCompra()                   { return null; }
    public ArrayList<Compra> consultarHistorial() { return new ArrayList<>(); }
    public void descargarReporte() {
        System.out.println("Generando reporte para " + nombre + "...");
    }

    public void agregarMetodoPago(MetodoPago mp) { metodosPago.add(mp); }
    public ArrayList<MetodoPago> getMetodosPago() { return metodosPago; }
}