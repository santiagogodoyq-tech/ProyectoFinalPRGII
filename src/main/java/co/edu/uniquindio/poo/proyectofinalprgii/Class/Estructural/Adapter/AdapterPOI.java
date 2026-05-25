package co.edu.uniquindio.poo.proyectofinalprgii.Class.Estructural.Adapter;

import java.util.List;
import java.util.Map;

public class AdapterPOI implements AdapterGenerador {

    // Stub que representa ApachePOIWorkbook
    private final Object poiLib = new Object();

    @Override
    public void exportar(List<Map<String, Object>> datos, String ruta) {
        System.out.println("[AdaptadorPOI] Exportando " + datos.size() + " registros a Excel → " + ruta);
        // Lógica real: crear XSSFWorkbook, rellenar celdas y guardar en ruta
        datos.forEach(fila -> System.out.println("  Fila: " + fila));
        System.out.println("[AdaptadorPOI] Archivo Excel generado en: " + ruta);
    }
}
