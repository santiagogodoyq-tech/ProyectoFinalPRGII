package co.edu.uniquindio.poo.proyectofinalprgii.Class;

import java.time.LocalDateTime;
import java.util.*;

public class ServicioMetricas {
    private final List<Map<String, String>> eventos = new ArrayList<>();

    public void registrarEvento(String entidad, String id, String anterior, String nuevo) {
        Map<String, String> ev = new LinkedHashMap<>();
        ev.put("entidad", entidad);
        ev.put("id", id);
        ev.put("anterior", anterior);
        ev.put("nuevo", nuevo);
        ev.put("timestamp", LocalDateTime.now().toString());
        eventos.add(ev);
    }

    public Map<String, Long> consultarVentasPorPeriodo(LocalDateTime ini, LocalDateTime fin) {
        System.out.printf("Consultando ventas entre %s y %s%n", ini, fin);
        return new HashMap<>();
    }

    public Map<String, Integer> consultarOcupacionPorZona(String idEvento) {
        System.out.println("Consultando ocupación para evento: " + idEvento);
        return new HashMap<>();
    }

    public double consultarTasaCancelacion() {
        long canceladas = eventos.stream().filter(e -> "CANCELADA".equals(e.get("nuevo"))).count();
        long totales = eventos.stream().filter(e -> "Compra".equals(e.get("entidad"))).count();
        return totales == 0 ? 0 : (double) canceladas / totales * 100;
    }

    public List<String> consultarTopEventos() {
        System.out.println("Consultando top eventos...");
        return new ArrayList<>();
    }
}
