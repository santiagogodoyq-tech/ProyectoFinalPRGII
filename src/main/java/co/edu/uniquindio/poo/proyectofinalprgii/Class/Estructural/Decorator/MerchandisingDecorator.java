package co.edu.uniquindio.poo.proyectofinalprgii.Class.Estructural.Decorator;

public class MerchandisingDecorator extends ServicioDecorator {
    private static final double COSTO = 25_000;

    public MerchandisingDecorator(EntradaServicioDecorator entrada) {
        super(entrada);
    }

    @Override
    public double calcularPrecio() {
        return entrada.calcularPrecio() + COSTO;
    }

    @Override
    public String describir() {
        return entrada.describir() + " + Kit Merchandising";
    }
}

