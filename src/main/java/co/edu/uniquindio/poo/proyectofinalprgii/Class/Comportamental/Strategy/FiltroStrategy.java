package co.edu.uniquindio.poo.proyectofinalprgii.Class.Comportamental.Strategy;
import java.util.List;
public interface FiltroStrategy<T> {
    List<T> filtrar(List<T> lista);
}
