package co.edu.uniquindio.poo.proyectofinalprgii.Class.Comportamental.State;

import co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas.Compra;


public interface StateCompra {
    void pagar(Compra compra, Object datos);
    void cancelar(Compra compra);
    void reembolsar(Compra compra);
}
