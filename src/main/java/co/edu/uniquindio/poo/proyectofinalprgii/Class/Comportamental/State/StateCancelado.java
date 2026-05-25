package co.edu.uniquindio.poo.proyectofinalprgii.Class.Comportamental.State;

import co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas.Compra;

public class StateCancelado implements StateCompra{
    @Override
    public void pagar(Compra compra, Object datos) {
        System.out.println("No se puede pagar una compra cancelada.");
    }

    @Override
    public void cancelar(Compra compra) {
        System.out.println("La compra ya está cancelada.");
    }

    @Override
    public void reembolsar(Compra compra) {
        System.out.println("No se puede reembolsar una compra cancelada sin pago.");
    }
}
