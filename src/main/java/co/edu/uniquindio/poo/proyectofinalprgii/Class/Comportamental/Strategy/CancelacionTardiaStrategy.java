package co.edu.uniquindio.poo.proyectofinalprgii.Class.Comportamental.Strategy;

import co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas.Compra;

import java.time.LocalDateTime;

public class CancelacionTardiaStrategy implements CancelacionStrategy {
    @Override
    public double calcularReembolso(Compra compra, LocalDateTime ahora) {
        double reembolso = compra.getTotal() * 0.20;
        System.out.printf("Cancelación tardía: reembolso del 20%% = $%.2f%n", reembolso);
        return reembolso;
    }
}
