package co.edu.uniquindio.poo.proyectofinalprgii.Class.Estructural.Decorator;

public class ParqueaderoDecorator extends ServicioDecorator{

    private static final double COSTO = 20_000;

    public ParqueaderoDecorator(EntradaServicioDecorator entrada) {
        super(entrada);
    }

    @Override
    public double calcularPrecio() {
        return entrada.calcularPrecio() + COSTO;
    }

    @Override
    public String describir() {
        return entrada.describir() + " + Parqueadero";
    }
}
