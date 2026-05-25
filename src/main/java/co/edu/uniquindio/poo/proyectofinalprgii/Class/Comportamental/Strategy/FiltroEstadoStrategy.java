package co.edu.uniquindio.poo.proyectofinalprgii.Class.Comportamental.Strategy;

import co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas.Compra;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.Enums.EstadoCompra;

import java.util.List;
import java.util.stream.Collectors;

public class FiltroEstadoStrategy implements FiltroStrategy<Compra> {
    private final EstadoCompra estadoFiltro;

    public FiltroEstadoStrategy(EstadoCompra estadoFiltro) {
        this.estadoFiltro = estadoFiltro;
    }

    @Override
    public List<Compra> filtrar(List<Compra> lista) {
        return lista.stream()
                .filter(c -> c.getEstado() == estadoFiltro)
                .collect(Collectors.toList());
    }
}
