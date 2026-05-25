package co.edu.uniquindio.poo.proyectofinalprgii.Class.Estructural.Adapter;

import java.util.List;
import java.util.Map;

public interface AdapterGenerador {
    void exportar(List<Map<String, Object>> datos, String ruta);
}
