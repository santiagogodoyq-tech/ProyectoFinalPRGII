package co.edu.uniquindio.poo.proyectofinalprgii.Class.Comportamental.Strategy;

import co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas.Compra;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class FiltroFechaStrategy implements FiltroStrategy<Compra> {
    private final LocalDateTime desde;
    private final LocalDateTime hasta;

    public FiltroFechaStrategy(LocalDateTime desde, LocalDateTime hasta) {
        this.desde = desde;
        this.hasta = hasta;
    }

    @Override
    public List<Compra> filtrar(List<Compra> lista) {
        return lista.stream()
                .filter(c -> !c.getFecha().isBefore(desde) && !c.getFecha().isAfter(hasta))
                .collect(Collectors.toList());
    }}
