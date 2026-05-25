package co.edu.uniquindio.poo.proyectofinalprgii.Class;

import co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas.Compra;

public class ResultadoCompra {
    private final boolean exitoso;
    private final Compra compra;
    private final String mensaje;

    public ResultadoCompra(boolean exitoso, Compra compra, String mensaje) {
        this.exitoso = exitoso;
        this.compra = compra;
        this.mensaje = mensaje;
    }

    public boolean isExitoso() { return exitoso; }
    public Compra getCompra()  { return compra; }
    public String getMensaje() { return mensaje; }

    @Override
    public String toString() {
        return String.format("ResultadoCompra{exitoso=%s, compra=%s, mensaje='%s'}",
                exitoso, compra != null ? compra.getIdCompra() : "null", mensaje);
    }
}
