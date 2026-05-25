package co.edu.uniquindio.poo.proyectofinalprgii.Class.Estructural.Decorator;

import co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas.Zona;
import org.controlsfx.control.decoration.Decorator;

public class EntradaBaseDecorator implements EntradaServicioDecorator{
    private final Zona zona;
    private final double precioBase;

    public EntradaBaseDecorator(Zona zona, double precioBase) {
        this.zona = zona;
        this.precioBase = precioBase;
    }

    @Override
    public double calcularPrecio() {
        return precioBase;
    }

    @Override
    public String describir() {
        return "Entrada zona " + zona.getNombre();
    }
}
