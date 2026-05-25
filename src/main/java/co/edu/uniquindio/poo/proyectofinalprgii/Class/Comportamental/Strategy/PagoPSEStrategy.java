package co.edu.uniquindio.poo.proyectofinalprgii.Class.Comportamental.Strategy;

import co.edu.uniquindio.poo.proyectofinalprgii.Class.ResultadoPago;

import java.util.UUID;

public class PagoPSEStrategy implements PagoStrategy {
    @Override
    public ResultadoPago procesar(double monto, Object datos) {
        System.out.printf("Procesando pago PSE: $%.2f%n", monto);
        return new ResultadoPago(true, "PSE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(), "Pago PSE aprobado");
    }
}
