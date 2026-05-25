package co.edu.uniquindio.poo.proyectofinalprgii.UI;

import co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas.Asiento;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas.Evento;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas.Zona;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.Enums.EstadoAsiento;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Diálogo visual de selección de asientos en matriz.
 * Filas = A-J, columnas = 1-5
 * Zonas: VIP (adelante), PREFERENCIAL (medio), GENERAL (atrás)
 * Sectores: ORIENTAL | CENTRAL | OCCIDENTAL
 */
public class SelectorAsientosUI {

    private final Evento evento;
    private final Consumer<Asiento> onAsientoSeleccionado;
    private Asiento seleccionado = null;
    private Button btnSeleccionado = null;

    public SelectorAsientosUI(Evento evento, Consumer<Asiento> onAsientoSeleccionado) {
        this.evento = evento;
        this.onAsientoSeleccionado = onAsientoSeleccionado;
    }

    public void mostrar() {
        Dialog<Asiento> dlg = new Dialog<>();
        dlg.setTitle("Selección de Asientos — " + evento.getNombre());
        dlg.setHeaderText(null);
        dlg.getDialogPane().setStyle("-fx-background-color: #0d1117; -fx-min-width: 820;");

        ScrollPane scroll = new ScrollPane(buildContenido(dlg));
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: #0d1117; -fx-background-color: #0d1117;");
        scroll.setPrefHeight(620);

        dlg.getDialogPane().setContent(scroll);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Button okBtn = (Button) dlg.getDialogPane().lookupButton(ButtonType.OK);
        okBtn.setText("Confirmar Asiento");
        okBtn.setDisable(true);
        okBtn.getStyleClass().add("btn-primary");

        dlg.setResultConverter(bt -> bt == ButtonType.OK ? seleccionado : null);

        // Habilitar OK cuando hay selección
        dlg.getDialogPane().lookupButton(ButtonType.OK).setDisable(true);

        Optional<Asiento> resultado = dlg.showAndWait();
        resultado.ifPresent(onAsientoSeleccionado);
    }

    private VBox buildContenido(Dialog<?> dlg) {
        VBox contenido = new VBox(20);
        contenido.setPadding(new Insets(20));
        contenido.setStyle("-fx-background-color: #0d1117;");

        // Título
        Label titulo = new Label("🎭  Selecciona tu asiento");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #a78bfa;");

        // Leyenda
        HBox leyenda = buildLeyenda();

        // Escenario
        Label escenario = new Label("▬▬▬▬▬▬▬▬  ESCENARIO  ▬▬▬▬▬▬▬▬");
        escenario.setStyle("-fx-text-fill: #f59e0b; -fx-font-size: 12px; " +
                "-fx-background-color: rgba(245,158,11,0.12); -fx-padding: 6 20; " +
                "-fx-background-radius: 6;");
        HBox escBox = new HBox(escenario);
        escBox.setAlignment(Pos.CENTER);

        // Info del asiento seleccionado
        Label lblSeleccion = new Label("Ningún asiento seleccionado");
        lblSeleccion.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 13px;");
        lblSeleccion.setId("lblSeleccion");

        // Matrices por zona y sector
        VBox matrizVIP  = buildSeccion("VIP", "#f59e0b", 50_000, dlg, lblSeleccion);
        VBox matrizPref = buildSeccion("PREFERENCIAL", "#a78bfa", 100_000, dlg, lblSeleccion);
        VBox matrizGen  = buildSeccion("GENERAL", "#10b981", 50_000, dlg, lblSeleccion);

        contenido.getChildren().addAll(titulo, leyenda, escBox, matrizVIP, matrizPref, matrizGen, lblSeleccion);
        return contenido;
    }

    private HBox buildLeyenda() {
        HBox leyenda = new HBox(20);
        leyenda.setAlignment(Pos.CENTER);
        leyenda.setPadding(new Insets(8));
        leyenda.setStyle("-fx-background-color: #161b22; -fx-background-radius: 8;");
        leyenda.getChildren().addAll(
                itemLeyenda("#10b981", "Disponible"),
                itemLeyenda("#ef4444", "Vendido/Reservado"),
                itemLeyenda("#7c3aed", "Seleccionado"),
                itemLeyenda("#f59e0b", "VIP"),
                itemLeyenda("#a78bfa", "Preferencial"),
                itemLeyenda("#3b82f6", "General")
        );
        return leyenda;
    }

    private HBox itemLeyenda(String color, String texto) {
        HBox item = new HBox(6);
        item.setAlignment(Pos.CENTER_LEFT);
        Rectangle rect = new Rectangle(14, 14, Color.web(color));
        rect.setArcWidth(4); rect.setArcHeight(4);
        Label lbl = new Label(texto);
        lbl.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 11px;");
        item.getChildren().addAll(rect, lbl);
        return item;
    }

    private VBox buildSeccion(String tipoZona, String color,
                              double precioBase, Dialog<?> dlg, Label lblSeleccion) {
        VBox seccion = new VBox(10);
        seccion.setPadding(new Insets(12));
        seccion.setStyle("-fx-background-color: #161b22; -fx-background-radius: 10; " +
                "-fx-border-color: rgba(255,255,255,0.07); -fx-border-radius: 10;");

        Label zonaTitulo = new Label(tipoZona + "  ·  $" +
                String.format("%,.0f", precioBase * 1.24));
        zonaTitulo.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; " +
                "-fx-text-fill: " + color + ";");

        // Sectores: ORIENTAL | CENTRAL | OCCIDENTAL
        String[] sectores = {"ORIENTAL", "CENTRAL", "OCCIDENTAL"};
        HBox sectoresBox = new HBox(12);
        sectoresBox.setAlignment(Pos.CENTER);

        for (String sector : sectores) {
            VBox sectorBox = new VBox(6);
            sectorBox.setAlignment(Pos.TOP_CENTER);
            sectorBox.setPadding(new Insets(8));
            sectorBox.setStyle("-fx-background-color: #0d1117; -fx-background-radius: 8; " +
                    "-fx-min-width: 200;");

            Label sectorLbl = new Label(sector);
            sectorLbl.setStyle("-fx-font-size: 10px; -fx-text-fill: #8b949e; -fx-font-weight: bold;");

            // Obtener asientos de esta zona y sector
            List<Asiento> asientosZonaSector = getAsientos(tipoZona, sector);

            // Encabezados de columnas
            HBox cols = new HBox(4);
            cols.setAlignment(Pos.CENTER);
            cols.getChildren().add(spacerLabel("  "));
            for (int c = 1; c <= 5; c++) {
                Label cl = new Label(String.valueOf(c));
                cl.setStyle("-fx-font-size: 9px; -fx-text-fill: #8b949e; " +
                        "-fx-min-width: 26; -fx-alignment: center;");
                cols.getChildren().add(cl);
            }

            VBox filas = new VBox(3);
            filas.setAlignment(Pos.CENTER);
            String[] letras = {"A","B","C","D","E","F","G","H","I","J"};
            for (String fila : letras) {
                HBox filaBox = new HBox(4);
                filaBox.setAlignment(Pos.CENTER);
                Label filaLbl = new Label(fila);
                filaLbl.setStyle("-fx-font-size: 9px; -fx-text-fill: #8b949e; " +
                        "-fx-min-width: 16; -fx-alignment: center;");
                filaBox.getChildren().add(filaLbl);

                for (int col = 1; col <= 5; col++) {
                    final int colFinal = col;
                    final String filaFinal = fila;
                    Asiento a = asientosZonaSector.stream()
                            .filter(as -> as.getFila().equals(filaFinal) && as.getNumero() == colFinal)
                            .findFirst().orElse(null);

                    Button btn = new Button();
                    btn.setMinSize(26, 22); btn.setMaxSize(26, 22);
                    btn.setPadding(Insets.EMPTY);

                    if (a == null || a.getEstado() == EstadoAsiento.VENDIDO
                            || a.getEstado() == EstadoAsiento.RESERVADO
                            || a.getEstado() == EstadoAsiento.BLOQUEADO) {
                        btn.setStyle("-fx-background-color: #ef4444; " +
                                "-fx-background-radius: 4; -fx-cursor: default;");
                        btn.setDisable(true);
                    } else {
                        btn.setStyle("-fx-background-color: " + color + "88; " +
                                "-fx-background-radius: 4; -fx-cursor: hand;");
                        final Asiento asientoFinal = a;
                        btn.setOnAction(e -> {
                            // Deseleccionar anterior
                            if (btnSeleccionado != null)
                                btnSeleccionado.setStyle(btnSeleccionado.getStyle()
                                        .replace("#7c3aed", color + "88"));
                            // Seleccionar nuevo
                            btn.setStyle("-fx-background-color: #7c3aed; " +
                                    "-fx-background-radius: 4;");
                            btnSeleccionado = btn;
                            seleccionado = asientoFinal;
                            lblSeleccion.setText("✅ Seleccionado: " + asientoFinal.getIdAsiento()
                                    + "  ·  " + tipoZona + "  ·  " + sector
                                    + "  ·  Fila " + asientoFinal.getFila()
                                    + " Col " + asientoFinal.getNumero());
                            lblSeleccion.setStyle("-fx-text-fill: #10b981; -fx-font-size: 13px;");
                            dlg.getDialogPane().lookupButton(ButtonType.OK).setDisable(false);
                        });
                    }
                    filaBox.getChildren().add(btn);
                }
                filas.getChildren().add(filaBox);
            }
            sectorBox.getChildren().addAll(sectorLbl, cols, filas);
            sectoresBox.getChildren().add(sectorBox);
        }

        seccion.getChildren().addAll(zonaTitulo, sectoresBox);
        return seccion;
    }

    private List<Asiento> getAsientos(String tipoZona, String sector) {
        return evento.getRecinto().getZonas().stream()
                .flatMap(z -> z.getAsientos().stream())
                .filter(a -> a.getTipoZona().equals(tipoZona) && a.getSector().equals(sector))
                .toList();
    }

    private Label spacerLabel(String t) {
        Label l = new Label(t);
        l.setMinWidth(16);
        return l;
    }
}