package co.edu.uniquindio.poo.proyectofinalprgii.Class.Comportamental.Strategy;

import co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas.Compra;


import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class CancelacionAnticipadaStrategy implements CancelacionStrategy {
    @Override
    public double calcularReembolso(Compra compra, LocalDateTime ahora) {
        long diasRestantes = ChronoUnit.DAYS.between(ahora, compra.getEvento().getFecha());
        double porcentaje = diasRestantes >= 7 ? 1.0 : 0.75;
        double reembolso = compra.getTotal() * porcentaje;
        System.out.printf("Cancelación anticipada: reembolso del %.0f%% = $%.2f%n", porcentaje * 100, reembolso);
        return reembolso;
    }
}
