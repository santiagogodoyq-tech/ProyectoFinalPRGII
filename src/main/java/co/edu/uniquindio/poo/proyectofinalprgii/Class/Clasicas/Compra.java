package co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas;

import co.edu.uniquindio.poo.proyectofinalprgii.Class.Comportamental.Observer.*;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.Comportamental.State.*;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.Comportamental.Strategy.CancelacionStrategy;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.Comportamental.Strategy.PagoStrategy;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.Enums.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Compra {

    private String idCompra;
    private LocalDateTime fecha;
    private double total;
    private EstadoCompra estado;
    private ArrayList<Entrada> entradas;
    private Evento evento;
    private UsuarioFinal usuario;
    private Pago pago;

    private StateCompra estadoHandler;
    private PagoStrategy estrategiaPago;
    private CancelacionStrategy estrategiaCancelacion;
    private ArrayList<ObserverEstado> observadores;
    private boolean conMerchandising = false;
    private boolean conSeguro        = false;
    private boolean conParqueadero   = false;

    public Compra(String idCompra, LocalDateTime fecha, Evento evento, UsuarioFinal usuario) {
        this.idCompra = idCompra;
        this.fecha = fecha;
        this.evento = evento;
        this.usuario = usuario;
        this.estado = EstadoCompra.CREADA;
        this.entradas = new ArrayList<>();
        this.observadores = new ArrayList<>();
    }

    public void agregarObservador(ObserverEstado obs) {
        observadores.add(obs);
    }

    private void notificarObservadores(EstadoCompra anterior, EstadoCompra nuevo) {
        for (ObserverEstado obs : observadores) {
            obs.actualizar("Compra", idCompra, anterior.name(), nuevo.name());
        }
    }

    // ── Libera todos los asientos de las entradas de esta compra ──
    private void liberarAsientos() {
        for (Entrada entrada : entradas) {
            Asiento a = entrada.getAsiento();
            if (a != null) {
                a.cambiarEstado(EstadoAsiento.DISPONIBLE);
            }
        }
    }

    public void pagar(Object datos) {
        if (estadoHandler != null) {
            estadoHandler.pagar(this, datos);
        } else {
            EstadoCompra anterior = this.estado;
            this.estado = EstadoCompra.PAGADA;
            notificarObservadores(anterior, this.estado);
        }
    }

    public void cancelar() {
        if (estadoHandler != null) {
            estadoHandler.cancelar(this);
        } else {
            EstadoCompra anterior = this.estado;
            this.estado = EstadoCompra.CANCELADA;
            notificarObservadores(anterior, this.estado);
        }
        // ── BUGFIX: liberar asientos al cancelar ──
        liberarAsientos();
    }

    public void reembolsar() {
        if (estadoHandler != null) {
            estadoHandler.reembolsar(this);
        } else {
            EstadoCompra anterior = this.estado;
            this.estado = EstadoCompra.REEMBOLSADA;
            notificarObservadores(anterior, this.estado);
        }
        // ── BUGFIX: liberar asientos al reembolsar ──
        liberarAsientos();
    }

    public void agregarEntrada(Entrada entrada) {
        entradas.add(entrada);
        recalcularTotal();
    }

    private void recalcularTotal() {
        this.total = entradas.stream().mapToDouble(Entrada::getPrecioFinal).sum();
    }

    public void setEstadoEnum(EstadoCompra nuevoEstado) {
        EstadoCompra anterior = this.estado;
        this.estado = nuevoEstado;
        notificarObservadores(anterior, nuevoEstado);
    }

    public void setEstadoHandler(StateCompra handler)            { this.estadoHandler = handler; }
    public void setEstrategiaPago(PagoStrategy estrategiaPago)   { this.estrategiaPago = estrategiaPago; }
    public void setEstrategiaCancelacion(CancelacionStrategy ec) { this.estrategiaCancelacion = ec; }
    public void setPago(Pago pago)                               { this.pago = pago; }

    public String getIdCompra()                   { return idCompra; }
    public LocalDateTime getFecha()               { return fecha; }
    public double getTotal()                      { return total; }
    public EstadoCompra getEstado()               { return estado; }
    public ArrayList<Entrada> getEntradas()       { return entradas; }
    public Evento getEvento()                     { return evento; }
    public UsuarioFinal getUsuario()              { return usuario; }
    public Pago getPago()                         { return pago; }
    public PagoStrategy getEstrategiaPago()       { return estrategiaPago; }
    public CancelacionStrategy getEstrategiaCancelacion() { return estrategiaCancelacion; }
    public void setTotal(double total)            { this.total = total; }
    public boolean isConMerchandising() { return conMerchandising; }
    public boolean isConSeguro()        { return conSeguro; }
    public boolean isConParqueadero()   { return conParqueadero; }

    public void guardarServicios(boolean merch, boolean seguro, boolean parqueo) {
        this.conMerchandising = merch;
        this.conSeguro        = seguro;
        this.conParqueadero   = parqueo;
        double extra = (merch   ? 25_000 : 0)
                + (seguro  ? 15_000 : 0)
                + (parqueo ? 10_000 : 0);
        if (extra > 0) this.total += extra;
    }
}