package co.edu.uniquindio.poo.proyectofinalprgii.Class.Creacional.FactoryMethod;

import co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas.Asiento;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas.Entrada;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas.Zona;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.PrecioEntrada;

import java.util.UUID;

public class FactoryEntradaVip extends FactoryEntrada {
    @Override
    public Entrada crearEntrada(Zona zona, Asiento asiento) {
        PrecioEntrada precio = new PrecioEntrada(200_000, 36_000, 10_000);
        return new Entrada("ENT-VIP-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase(), zona, asiento, precio);
    }
}
