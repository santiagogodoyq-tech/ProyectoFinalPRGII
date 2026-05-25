package co.edu.uniquindio.poo.proyectofinalprgii.Class.Comportamental.State;

import co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas.Compra;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.Creacional.Builder.CompraBuilder;

public class StateReembolsada implements StateCompra{
    @Override
    public void pagar(Compra compra, Object datos) {
        System.out.println("Compra ya reembolsada, no se puede pagar.");
    }

    @Override
    public void cancelar(Compra compra) {
        System.out.println("Compra ya reembolsada.");
    }

    @Override
    public void reembolsar(Compra compra) {
        System.out.println("El reembolso ya fue procesado.");
    }
}
