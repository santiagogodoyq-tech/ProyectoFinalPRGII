package co.edu.uniquindio.poo.proyectofinalprgii.Class.Estructural.Decorator;

public class SeguroCanceladorDecorator extends ServicioDecorator {
    private static final double COSTO = 15_000;

    public SeguroCanceladorDecorator(EntradaServicioDecorator entrada) {
        super(entrada);
    }

    @Override
    public double calcularPrecio() {
        return entrada.calcularPrecio() + COSTO;
    }

    @Override
    public String describir() {
        return entrada.describir() + " + Seguro de Cancelación";
    }
}
