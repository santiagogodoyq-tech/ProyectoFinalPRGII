package co.edu.uniquindio.poo.proyectofinalprgii.Class.Creacional.FactoryMethod;

import co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas.Asiento;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas.Compra;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas.Entrada;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas.Zona;

public abstract class FactoryEntrada {
    public abstract Entrada crearEntrada(Zona zona, Asiento asiento);

    public Entrada procesarEntrada(Compra compra, Zona zona, Asiento asiento) {
        Entrada entrada = crearEntrada(zona, asiento);
        compra.agregarEntrada(entrada);
        System.out.println("Entrada procesada: " + entrada.getIdEntrada() + " — $" + entrada.getPrecioFinal());
        return entrada;
    }
}
