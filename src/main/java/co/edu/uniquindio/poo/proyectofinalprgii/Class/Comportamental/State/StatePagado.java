package co.edu.uniquindio.poo.proyectofinalprgii.Class.Comportamental.State;

import co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas.Compra;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.Creacional.Builder.CompraBuilder;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.Enums.EstadoCompra;

import java.time.LocalDateTime;

public class StatePagado implements StateCompra {
    @Override
    public void pagar(Compra compra, Object datos) {
        System.out.println("La compra ya fue pagada.");
    }

    @Override
    public void cancelar(Compra compra) {
        double reembolso = 0;
        if (compra.getEstrategiaCancelacion() != null) {
            reembolso = compra.getEstrategiaCancelacion().calcularReembolso(compra, LocalDateTime.now());
        }
        compra.setEstadoEnum(EstadoCompra.CANCELADA);
        compra.setEstadoHandler(new StateCancelado());
        System.out.printf("Compra cancelada. Reembolso: $%.2f%n", reembolso);
    }

    @Override
    public void reembolsar(Compra compra) {
        compra.setEstadoEnum(EstadoCompra.REEMBOLSADA);
        compra.setEstadoHandler(new StateReembolsada());
        System.out.println("Reembolso procesado.");
    }
}
