package co.edu.uniquindio.poo.proyectofinalprgii.Class.Creacional.Builder;

import co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas.*;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.Comportamental.Observer.ObserverEstado;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.Comportamental.State.StateCreado;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.Comportamental.Strategy.CancelacionStrategy;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.Comportamental.Strategy.PagoStrategy;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.Comportamental.Strategy.PagoTarjetaStrategy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

public class CompraBuilder {
    private UsuarioFinal usuario;
    private Evento evento;
    private final ArrayList<Entrada> entradas = new ArrayList<>();
    private Pago pago;
    private PagoStrategy estrategiaPago;
    private CancelacionStrategy estrategiaCancelacion;
    private final ArrayList<ObserverEstado> observadores = new ArrayList<>();

    public CompraBuilder conUsuario(UsuarioFinal usuario) {
        this.usuario = usuario;
        return this;
    }

    public CompraBuilder conEvento(Evento evento) {
        this.evento = evento;
        return this;
    }

    public CompraBuilder conEntrada(Entrada entrada) {
        this.entradas.add(entrada);
        return this;
    }

    public CompraBuilder conPago(Pago pago) {
        this.pago = pago;
        return this;
    }

    public CompraBuilder conEstrategiaPago(PagoStrategy estrategiaPago) {
        this.estrategiaPago = estrategiaPago;
        return this;
    }

    public CompraBuilder conEstrategiaCancelacion(CancelacionStrategy estrategiaCancelacion) {
        this.estrategiaCancelacion = estrategiaCancelacion;
        return this;
    }

    public CompraBuilder conObservador(ObserverEstado observador) {
        this.observadores.add(observador);
        return this;
    }

    public Compra build() {
        if (usuario == null || evento == null) {
            throw new IllegalStateException("Usuario y Evento son obligatorios para construir una Compra.");
        }
        String idCompra = "COMP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Compra compra = new Compra(idCompra, LocalDateTime.now(), evento, usuario);

        entradas.forEach(compra::agregarEntrada);
        observadores.forEach(compra::agregarObservador);

        compra.setPago(pago);
        compra.setEstrategiaPago(estrategiaPago);
        compra.setEstrategiaCancelacion(estrategiaCancelacion);
        compra.setEstadoHandler(new StateCreado());

        return compra;
    }
}
