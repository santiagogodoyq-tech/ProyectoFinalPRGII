package co.edu.uniquindio.poo.proyectofinalprgii.Class.Comportamental.Strategy;

import co.edu.uniquindio.poo.proyectofinalprgii.Class.ResultadoPago;

import java.util.UUID;

public class PagoEfectivoStrategy implements PagoStrategy {
    @Override
    public ResultadoPago procesar(double monto, Object datos) {
        System.out.printf("Generando cupón pago en efectivo: $%.2f%n", monto);
        return new ResultadoPago(true, "EFE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(), "Cupón generado");
    }

}
