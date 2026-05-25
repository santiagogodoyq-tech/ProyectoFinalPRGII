package co.edu.uniquindio.poo.proyectofinalprgii.Class.Estructural.Facade;

import co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas.*;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.Comportamental.Observer.ObserverEstado;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.Comportamental.Strategy.PagoStrategy;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.Creacional.Builder.CompraBuilder;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.Creacional.FactoryMethod.FactoryEntrada;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.Creacional.Singleton.GestorDisponibilidad;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.Enums.EstadoCompra;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.MetodoPago;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.ResultadoCompra;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.ResultadoPago;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ServicioCompraFacade {
    private final GestorDisponibilidad disponibilidad;
    private final PagoStrategy estrategiaPago;
    private final FactoryEntrada creadorEntrada;
    private final ArrayList<ObserverEstado> observadores;
    private final Map<String, Compra> repositorioCompras;

    public ServicioCompraFacade(PagoStrategy estrategiaPago,
                                FactoryEntrada creadorEntrada,
                                ArrayList<ObserverEstado> observadores) {
        this.disponibilidad      = GestorDisponibilidad.getInstancia();
        this.estrategiaPago      = estrategiaPago;
        this.creadorEntrada      = creadorEntrada;
        this.observadores        = observadores;
        this.repositorioCompras  = new HashMap<>();
    }

    public ResultadoCompra confirmarCompra(UsuarioFinal usuario,
                                           Evento evento,
                                           Zona zona,
                                           Asiento asiento,
                                           MetodoPago metodoPago) {
        // 1. Reservar asiento
        boolean reservado = disponibilidad.reservar(asiento.getIdAsiento());
        if (!reservado) {
            return new ResultadoCompra(false, null, "El asiento no está disponible.");
        }

        // 2. Crear entrada mediante Factory Method
        Entrada entrada = creadorEntrada.crearEntrada(zona, asiento);

        // 3. Construir la compra con el Builder
        CompraBuilder builder = new CompraBuilder()
                .conUsuario(usuario)
                .conEvento(evento)
                .conEntrada(entrada)
                .conEstrategiaPago(estrategiaPago);

        observadores.forEach(builder::conObservador);
        Compra compra = builder.build();

        // 4. Procesar pago
        ResultadoPago resultadoPago = estrategiaPago.procesar(compra.getTotal(), metodoPago);
        if (!resultadoPago.isExitoso()) {
            disponibilidad.liberar(asiento.getIdAsiento());
            return new ResultadoCompra(false, compra, "Pago rechazado: " + resultadoPago.getMensaje());
        }

        // 5. Registrar el pago en la compra
        Pago pago = new Pago(
                "PAG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                compra.getTotal(),
                LocalDateTime.now(),
                resultadoPago.getReferencia(),
                metodoPago
        );
        pago.confirmar();
        compra.setPago(pago);
        compra.setEstadoEnum(EstadoCompra.PAGADA);

        // 6. Generar QR de la entrada
        entrada.generarQR();

        // 7. Persistir en repositorio
        repositorioCompras.put(compra.getIdCompra(), compra);

        return new ResultadoCompra(true, compra, "Compra confirmada con referencia " + pago.getReferencia());
    }

    public void cancelarCompra(String idCompra) {
        Compra compra = repositorioCompras.get(idCompra);
        if (compra == null) {
            System.out.println("Compra no encontrada: " + idCompra);
            return;
        }
        compra.cancelar();
        // Liberar asientos
        compra.getEntradas().forEach(e -> {
            if (e.getAsiento() != null) {
                disponibilidad.liberar(e.getAsiento().getIdAsiento());
            }
        });
    }

    public Compra consultarCompra(String idCompra) {
        return repositorioCompras.get(idCompra);
    }
}
