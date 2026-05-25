package co.edu.uniquindio.poo.proyectofinalprgii.Class.Creacional.FactoryMethod;

import co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas.Asiento;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas.Entrada;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas.Zona;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.PrecioEntrada;

import java.util.UUID;

public class FactoryEntradaPreferencial extends FactoryEntrada {
    @Override
    public Entrada crearEntrada(Zona zona, Asiento asiento) {
        PrecioEntrada precio = new PrecioEntrada(100_000, 18_000, 6_000);
        return new Entrada("ENT-PRE-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase(), zona, asiento, precio);
    }
}
