package co.edu.uniquindio.poo.proyectofinalprgii.Class;

import co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas.Compra;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Genera reportes de compras en CSV o PDF (texto plano simulado).
 * Para PDF real, conectar AdapterPDFBox con la librería pdfbox.
 */
public class ReporteServicio {

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // ── CSV ────────────────────────────────────────────────────────
    public static void exportarCSV(List<Compra> compras, Window owner) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Guardar reporte CSV");
        fc.setInitialFileName("mis_compras.csv");
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV (*.csv)", "*.csv"));
        File file = fc.showSaveDialog(owner);
        if (file == null) return;

        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            pw.println("ID Compra,Evento,Fecha,Total,Estado,Asiento,Zona");
            for (Compra c : compras) {
                String asiento = c.getEntradas().isEmpty() ? "—"
                        : (c.getEntradas().get(0).getAsiento() != null
                        ? c.getEntradas().get(0).getAsiento().getIdAsiento()
                        : "—");
                String zona = c.getEntradas().isEmpty() ? "—"
                        : c.getEntradas().get(0).getZona().getNombre();
                pw.printf("%s,\"%s\",%s,$%,.0f,%s,%s,%s%n",
                        c.getIdCompra(),
                        c.getEvento().getNombre(),
                        c.getFecha().format(FMT),
                        c.getTotal(),
                        c.getEstado().name(),
                        asiento, zona);
            }
            System.out.println("CSV guardado en: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error al guardar CSV: " + e.getMessage());
        }
    }

    // ── PDF (texto plano como stub, reemplazar con pdfbox) ─────────
    public static void exportarPDF(List<Compra> compras, Window owner) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Guardar reporte PDF");
        fc.setInitialFileName("mis_compras.txt");  // cambiar a .pdf con pdfbox
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Texto (*.txt)", "*.txt"));
        File file = fc.showSaveDialog(owner);
        if (file == null) return;

        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            pw.println("========================================");
            pw.println("        REPORTE DE COMPRAS - EventosPro");
            pw.println("========================================");
            pw.println();
            for (Compra c : compras) {
                pw.println("ID:      " + c.getIdCompra());
                pw.println("Evento:  " + c.getEvento().getNombre());
                pw.println("Fecha:   " + c.getFecha().format(FMT));
                pw.printf ("Total:   $%,.0f%n", c.getTotal());
                pw.println("Estado:  " + c.getEstado().name());
                if (!c.getEntradas().isEmpty()) {
                    var ent = c.getEntradas().get(0);
                    pw.println("Zona:    " + ent.getZona().getNombre());
                    if (ent.getAsiento() != null)
                        pw.println("Asiento: " + ent.getAsiento().getIdAsiento()
                                + " (" + ent.getAsiento().getSector() + ")");
                }
                pw.println("----------------------------------------");
            }
            System.out.println("PDF guardado en: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error al guardar PDF: " + e.getMessage());
        }
    }
}