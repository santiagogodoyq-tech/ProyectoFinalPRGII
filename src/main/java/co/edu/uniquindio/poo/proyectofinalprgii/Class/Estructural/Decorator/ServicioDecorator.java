package co.edu.uniquindio.poo.proyectofinalprgii.Class.Estructural.Decorator;

public abstract class ServicioDecorator implements EntradaServicioDecorator{
    protected final EntradaServicioDecorator entrada;

    protected ServicioDecorator(EntradaServicioDecorator entrada) {
        this.entrada = entrada;
    }

    @Override
    public double calcularPrecio() {
        return entrada.calcularPrecio();
    }

    @Override
    public String describir() {
        return entrada.describir();
    }

}
