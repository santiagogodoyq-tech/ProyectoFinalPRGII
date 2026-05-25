package co.edu.uniquindio.poo.proyectofinalprgii.Class.Comportamental.Strategy;

import co.edu.uniquindio.poo.proyectofinalprgii.Class.ResultadoPago;

public interface PagoStrategy {
    ResultadoPago procesar(double monto, Object datos);
}
