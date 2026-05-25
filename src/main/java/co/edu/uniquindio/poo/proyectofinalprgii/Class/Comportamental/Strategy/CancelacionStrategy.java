package co.edu.uniquindio.poo.proyectofinalprgii.Class.Comportamental.Strategy;

import co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas.Compra;

import java.time.LocalDateTime;

public interface CancelacionStrategy {
    double calcularReembolso(Compra compra, LocalDateTime ahora);
}
