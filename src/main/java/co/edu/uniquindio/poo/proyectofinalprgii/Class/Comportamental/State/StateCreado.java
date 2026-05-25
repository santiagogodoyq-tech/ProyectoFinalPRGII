package co.edu.uniquindio.poo.proyectofinalprgii.Class.Comportamental.State;

import co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas.Compra;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.Enums.EstadoCompra;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.ResultadoPago;

public class StateCreado implements StateCompra{
    @Override
    public void pagar(Compra compra, Object datos) {
        if (compra.getEstrategiaPago() != null) {
            ResultadoPago resultado = compra.getEstrategiaPago().procesar(compra.getTotal(), datos);
            if (resultado.isExitoso()) {
                compra.setEstadoEnum(EstadoCompra.PAGADA);
                compra.setEstadoHandler(new StatePagado());
                System.out.println("Compra pagada. Ref: " + resultado.getReferencia());
            } else {
                System.out.println("Pago rechazado: " + resultado.getMensaje());
            }
        }
    }

    @Override
    public void cancelar(Compra compra) {
        compra.setEstadoEnum(EstadoCompra.CANCELADA);
        compra.setEstadoHandler(new StateCancelado());
        System.out.println("Compra cancelada desde estado CREADA.");
    }

    @Override
    public void reembolsar(Compra compra) {
        System.out.println("No se puede reembolsar una compra que no ha sido pagada.");
    }
}
