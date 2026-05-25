package co.edu.uniquindio.poo.proyectofinalprgii.Class.Estructural.Adapter;

import java.util.List;
import java.util.Map;

public class AdapterPDFBox implements AdapterGenerador {
    private final Object pdfLib = new Object();

    @Override
    public void exportar(List<Map<String, Object>> datos, String ruta) {
        System.out.println("[AdaptadorPDFBox] Exportando " + datos.size() + " registros a PDF → " + ruta);
        // Lógica real: crear PDDocument, añadir páginas/contenido y guardar en ruta
        datos.forEach(fila -> System.out.println("  Registro: " + fila));
        System.out.println("[AdaptadorPDFBox] Archivo PDF generado en: " + ruta);
    }
}
