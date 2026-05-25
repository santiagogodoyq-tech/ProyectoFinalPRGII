package co.edu.uniquindio.poo.proyectofinalprgii.UI;

import co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas.*;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.Enums.EstadoAsiento;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class RecintosUI {
    private final DataStore ds = DataStore.get();
    private TableView<Recinto> table;

    public Node build() {
        VBox root = new VBox(24);
        root.setPadding(new Insets(28, 32, 32, 32));

        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Gestión de Recintos");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: -text-primary;");
        Region sp = UtilsUI.spacer();
        Button btnNuevo = new Button("+ Nuevo Recinto");
        btnNuevo.getStyleClass().add("btn-primary");
        btnNuevo.setOnAction(e -> showCreateDialog());
        header.getChildren().addAll(title, sp, btnNuevo);

        table = buildTable();
        VBox.setVgrow(table, Priority.ALWAYS);
        VBox card = UtilsUI.card(table);
        VBox.setVgrow(card, Priority.ALWAYS);
        root.getChildren().addAll(header, card);
        VBox.setVgrow(root, Priority.ALWAYS);
        return root;
    }

    @SuppressWarnings("unchecked")
    private TableView<Recinto> buildTable() {
        // ── BUGFIX: recolectar recintos únicos de todos los eventos ──
        javafx.collections.ObservableList<Recinto> recintosUnicos =
                javafx.collections.FXCollections.observableArrayList();
        for (Evento ev : ds.eventos) {
            Recinto r = ev.getRecinto();
            boolean yaExiste = recintosUnicos.stream()
                    .anyMatch(x -> x.getIdRecinto().equals(r.getIdRecinto()));
            if (!yaExiste) recintosUnicos.add(r);
        }

        TableView<Recinto> tv = new TableView<>(recintosUnicos);
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tv.setPrefHeight(9999);

        TableColumn<Recinto, String> colId = col("ID", 100);
        colId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getIdRecinto()));

        TableColumn<Recinto, String> colNombre = col("Nombre", 200);
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));

        TableColumn<Recinto, String> colCiudad = col("Ciudad", 120);
        colCiudad.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCiudad()));

        TableColumn<Recinto, String> colDir = col("Dirección", 180);
        colDir.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDireccion()));

        TableColumn<Recinto, String> colZonas = col("Zonas", 60);
        colZonas.setCellValueFactory(c -> new SimpleStringProperty(
                String.valueOf(c.getValue().getZonas().size())));

        // ── NUEVO: columna de asientos totales ──
        TableColumn<Recinto, String> colTotales = col("Total asientos", 110);
        colTotales.setCellValueFactory(c -> {
            long total = c.getValue().getZonas().stream()
                    .mapToLong(z -> z.getAsientos().size()).sum();
            return new SimpleStringProperty(String.valueOf(total));
        });

        // ── BUGFIX: columna libres ──
        TableColumn<Recinto, String> colLibres = col("Libres", 80);
        colLibres.setCellValueFactory(c -> {
            long libres = c.getValue().getZonas().stream()
                    .flatMap(z -> z.getAsientos().stream())
                    .filter(a -> a.getEstado() == EstadoAsiento.DISPONIBLE)
                    .count();
            return new SimpleStringProperty(String.valueOf(libres));
        });
        colLibres.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); return; }
                setText(item);
                setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold;");
            }
        });

        // ── NUEVO: columna ocupados (antes faltaba) ──
        TableColumn<Recinto, String> colOcupados = col("Ocupados", 80);
        colOcupados.setCellValueFactory(c -> {
            long ocupados = c.getValue().getZonas().stream()
                    .flatMap(z -> z.getAsientos().stream())
                    .filter(a -> a.getEstado() == EstadoAsiento.VENDIDO
                            || a.getEstado() == EstadoAsiento.RESERVADO)
                    .count();
            return new SimpleStringProperty(String.valueOf(ocupados));
        });
        colOcupados.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); return; }
                setText(item);
                setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
            }
        });

        // ── NUEVO: columna bloqueados ──
        TableColumn<Recinto, String> colBloqueados = col("Bloqueados", 90);
        colBloqueados.setCellValueFactory(c -> {
            long bloqueados = c.getValue().getZonas().stream()
                    .flatMap(z -> z.getAsientos().stream())
                    .filter(a -> a.getEstado() == EstadoAsiento.BLOQUEADO)
                    .count();
            return new SimpleStringProperty(String.valueOf(bloqueados));
        });
        colBloqueados.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); return; }
                setText(item);
                setStyle("-fx-text-fill: #f59e0b; -fx-font-weight: bold;");
            }
        });

        // Botón detalle de zonas
        TableColumn<Recinto, Void> colDetalle = new TableColumn<>("Detalle");
        colDetalle.setPrefWidth(100);
        colDetalle.setCellFactory(col -> new TableCell<>() {
            final Button btn = new Button("🏟 Zonas");
            { btn.setStyle("-fx-font-size:10px; -fx-padding: 4 10;"); }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                Recinto r = getTableView().getItems().get(getIndex());
                btn.setOnAction(e -> mostrarDetalleZonas(r));
                setGraphic(btn);
            }
        });

        tv.getColumns().addAll(colId, colNombre, colCiudad, colDir, colZonas,
                colTotales, colLibres, colOcupados, colBloqueados, colDetalle);
        return tv;
    }

    // ── NUEVO: diálogo de detalle de zonas con estadísticas ──
    private void mostrarDetalleZonas(Recinto r) {
        Dialog<Void> dlg = new Dialog<>();
        dlg.setTitle("Zonas de " + r.getNombre());
        dlg.setHeaderText(null);
        dlg.getDialogPane().setStyle("-fx-background-color: #1c2128; -fx-min-width: 560;");

        VBox contenido = new VBox(12);
        contenido.setPadding(new Insets(16));

        Label titulo = new Label("🏟  " + r.getNombre() + " — " + r.getCiudad());
        titulo.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #a78bfa;");
        contenido.getChildren().add(titulo);

        for (Zona z : r.getZonas()) {
            long total     = z.getAsientos().size();
            long libres    = z.getAsientos().stream().filter(a -> a.getEstado() == EstadoAsiento.DISPONIBLE).count();
            long ocupados  = z.getAsientos().stream().filter(a -> a.getEstado() == EstadoAsiento.VENDIDO || a.getEstado() == EstadoAsiento.RESERVADO).count();
            long bloqueados = z.getAsientos().stream().filter(a -> a.getEstado() == EstadoAsiento.BLOQUEADO).count();
            double pctOcup = total > 0 ? (double) ocupados / total * 100 : 0;

            VBox zonaBox = new VBox(6);
            zonaBox.setPadding(new Insets(10));
            zonaBox.setStyle("-fx-background-color: #161b22; -fx-background-radius: 8;");

            Label znm = new Label(z.getNombre() + "  (" + z.getIdZona() + ")");
            znm.setStyle("-fx-font-weight: bold; -fx-text-fill: #e6edf3;");

            HBox stats = new HBox(16);
            stats.getChildren().addAll(
                    statLabel("Total", String.valueOf(total), "#8b949e"),
                    statLabel("Libres", String.valueOf(libres), "#10b981"),
                    statLabel("Ocupados", String.valueOf(ocupados), "#ef4444"),
                    statLabel("Bloqueados", String.valueOf(bloqueados), "#f59e0b"),
                    statLabel("Ocupación", String.format("%.1f%%", pctOcup), "#a78bfa")
            );

            // Barra de ocupación
            StackPane baraBg = new StackPane();
            baraBg.setStyle("-fx-background-color: #0d1117; -fx-background-radius: 4;");
            baraBg.setPrefHeight(8);
            Region fill = new Region();
            fill.setPrefHeight(8);
            fill.setPrefWidth(pctOcup * 3); // escala visual
            fill.setMaxWidth(300);
            fill.setStyle("-fx-background-color: " + (pctOcup > 80 ? "#ef4444" : pctOcup > 50 ? "#f59e0b" : "#10b981") + "; -fx-background-radius: 4;");
            baraBg.getChildren().add(fill);
            StackPane.setAlignment(fill, Pos.CENTER_LEFT);

            zonaBox.getChildren().addAll(znm, stats, baraBg);
            contenido.getChildren().add(zonaBox);
        }

        ScrollPane scroll = new ScrollPane(contenido);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: #1c2128; -fx-background-color: #1c2128;");
        scroll.setPrefHeight(400);

        dlg.getDialogPane().setContent(scroll);
        dlg.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dlg.showAndWait();
    }

    private HBox statLabel(String label, String value, String color) {
        VBox box = new VBox(2);
        Label v = new Label(value);
        v.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        Label l = new Label(label);
        l.setStyle("-fx-font-size: 10px; -fx-text-fill: #8b949e;");
        box.getChildren().addAll(v, l);
        return new HBox(box);
    }

    private <T> TableColumn<Recinto, T> col(String title, double width) {
        TableColumn<Recinto, T> c = new TableColumn<>(title);
        c.setPrefWidth(width);
        return c;
    }

    private void showCreateDialog() {
        Dialog<Void> dlg = new Dialog<>();
        dlg.setTitle("Nuevo Recinto");
        dlg.setHeaderText(null);
        dlg.getDialogPane().setStyle("-fx-background-color: #1c2128;");

        GridPane grid = new GridPane();
        grid.setHgap(16); grid.setVgap(14);
        grid.setPadding(new Insets(20));

        TextField fNombre = field("Nombre del recinto");
        TextField fCiudad = field("Ciudad");
        TextField fDir    = field("Dirección");

        grid.add(lbl("Nombre"),    0, 0); grid.add(fNombre, 1, 0);
        grid.add(lbl("Ciudad"),    0, 1); grid.add(fCiudad, 1, 1);
        grid.add(lbl("Dirección"), 0, 2); grid.add(fDir,    1, 2);

        dlg.getDialogPane().setContent(grid);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Button ok = (Button) dlg.getDialogPane().lookupButton(ButtonType.OK);
        ok.setText("Crear"); ok.getStyleClass().add("btn-primary");

        dlg.setResultConverter(bt -> {
            if (bt == ButtonType.OK && !fNombre.getText().isBlank()) {
                String id = "R-" + String.format("%03d", ds.eventos.size() + 10);
                Recinto r = new Recinto(id, fNombre.getText().trim(), fDir.getText().trim(), fCiudad.getText().trim());
                ds.log("🏟 Recinto creado: " + r.getNombre());
                table.refresh();
            }
            return null;
        });
        dlg.showAndWait();
    }

    private TextField field(String prompt) {
        TextField tf = new TextField(); tf.setPromptText(prompt); tf.setPrefWidth(260); return tf;
    }
    private Label lbl(String t) {
        Label l = new Label(t); l.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 12px;"); return l;
    }
}