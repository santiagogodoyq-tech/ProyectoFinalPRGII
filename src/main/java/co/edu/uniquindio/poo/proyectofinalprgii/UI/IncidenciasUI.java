package co.edu.uniquindio.poo.proyectofinalprgii.UI;

import co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class IncidenciasUI {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final DataStore ds = DataStore.get();
    private final ObservableList<Incidencia> incidencias = FXCollections.observableArrayList();
    private TableView<Incidencia> table;

    public Node build() {
        VBox root = new VBox(24);
        root.setPadding(new Insets(28, 32, 32, 32));

        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Registro de Incidencias");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: -text-primary;");
        Region sp = UtilsUI.spacer();
        Button btnNueva = new Button("+ Registrar Incidencia");
        btnNueva.getStyleClass().add("btn-primary");
        btnNueva.setOnAction(e -> showCreateDialog());
        header.getChildren().addAll(title, sp, btnNueva);

        // Filtros
        TextField fBuscar = new TextField();
        fBuscar.setPromptText("🔍 Buscar...");
        fBuscar.setPrefWidth(220);
        ComboBox<String> cbTipo = new ComboBox<>();
        cbTipo.getItems().addAll("Todos", "PAGO", "ASIENTO", "CANCELACION", "SISTEMA");
        cbTipo.setValue("Todos");
        DatePicker dpDesde = new DatePicker();
        dpDesde.setPromptText("Desde");
        DatePicker dpHasta = new DatePicker();
        dpHasta.setPromptText("Hasta");
        HBox filtros = new HBox(10, fBuscar, cbTipo, dpDesde, dpHasta);
        filtros.setAlignment(Pos.CENTER_LEFT);

        table = buildTable();
        VBox.setVgrow(table, Priority.ALWAYS);

        // Conectar filtros
        Runnable refrescar = () -> {
            String txt = fBuscar.getText().toLowerCase().trim();
            String tipo = cbTipo.getValue();
            table.setItems(incidencias.filtered(inc -> {
                boolean okTxt  = txt.isEmpty() || inc.getDescripcion().toLowerCase().contains(txt)
                        || inc.getTipo().toLowerCase().contains(txt);
                boolean okTipo = "Todos".equals(tipo) || tipo.equals(inc.getTipo());
                boolean okD    = dpDesde.getValue() == null || !inc.getFecha().toLocalDate().isBefore(dpDesde.getValue());
                boolean okH    = dpHasta.getValue() == null || !inc.getFecha().toLocalDate().isAfter(dpHasta.getValue());
                return okTxt && okTipo && okD && okH;
            }));
        };
        fBuscar.textProperty().addListener((o, a, b) -> refrescar.run());
        cbTipo.setOnAction(e -> refrescar.run());
        dpDesde.setOnAction(e -> refrescar.run());
        dpHasta.setOnAction(e -> refrescar.run());

        VBox card = UtilsUI.card(filtros, table);
        VBox.setVgrow(card, Priority.ALWAYS);
        root.getChildren().addAll(header, card);
        VBox.setVgrow(root, Priority.ALWAYS);
        return root;
    }

    @SuppressWarnings("unchecked")
    private TableView<Incidencia> buildTable() {
        TableView<Incidencia> tv = new TableView<>(incidencias);
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tv.setPrefHeight(9999);
        tv.setPlaceholder(new Label("No hay incidencias registradas"));

        TableColumn<Incidencia, String> colId   = col("ID", 100);
        colId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getIdIncidencia()));
        TableColumn<Incidencia, String> colTipo = col("Tipo", 120);
        colTipo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTipo()));
        TableColumn<Incidencia, String> colDesc = col("Descripción", 280);
        colDesc.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescripcion()));
        TableColumn<Incidencia, String> colEnt  = col("Entidad", 110);
        colEnt.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEntidadAfectada()));
        TableColumn<Incidencia, String> colFecha = col("Fecha", 140);
        colFecha.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFecha().format(FMT)));

        tv.getColumns().addAll(colId, colTipo, colDesc, colEnt, colFecha);
        return tv;
    }

    private <T> TableColumn<Incidencia, T> col(String t, double w) {
        TableColumn<Incidencia, T> c = new TableColumn<>(t); c.setPrefWidth(w); return c;
    }

    private void showCreateDialog() {
        Dialog<Void> dlg = new Dialog<>();
        dlg.setTitle("Registrar Incidencia");
        dlg.setHeaderText(null);
        dlg.getDialogPane().setStyle("-fx-background-color: #1c2128;");

        GridPane grid = new GridPane();
        grid.setHgap(16); grid.setVgap(14);
        grid.setPadding(new Insets(20));

        ComboBox<String> cbTipo = new ComboBox<>();
        cbTipo.getItems().addAll("PAGO", "ASIENTO", "CANCELACION", "SISTEMA");
        cbTipo.setValue("SISTEMA");

        TextField fDesc = new TextField(); fDesc.setPromptText("Descripción"); fDesc.setPrefWidth(280);

        ComboBox<String> cbEntidad = new ComboBox<>();
        cbEntidad.getItems().addAll("EVENTO", "COMPRA", "USUARIO");
        cbEntidad.setValue("EVENTO");

        ComboBox<Evento> cbEvento = new ComboBox<>();
        cbEvento.getItems().addAll(ds.eventos);
        cbEvento.setConverter(new javafx.util.StringConverter<>() {
            public String toString(Evento e) { return e == null ? "" : e.getNombre(); }
            public Evento fromString(String s) { return null; }
        });

        ComboBox<Compra> cbCompra = new ComboBox<>();
        cbCompra.getItems().addAll(ds.compras);
        cbCompra.setConverter(new javafx.util.StringConverter<>() {
            public String toString(Compra c) { return c == null ? "" : c.getIdCompra(); }
            public Compra fromString(String s) { return null; }
        });

        grid.add(lbl("Tipo"),     0, 0); grid.add(cbTipo,    1, 0);
        grid.add(lbl("Descripción"), 0, 1); grid.add(fDesc,  1, 1);
        grid.add(lbl("Entidad"),  0, 2); grid.add(cbEntidad, 1, 2);
        grid.add(lbl("Evento"),   0, 3); grid.add(cbEvento,  1, 3);
        grid.add(lbl("Compra"),   0, 4); grid.add(cbCompra,  1, 4);

        dlg.getDialogPane().setContent(grid);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Button ok = (Button) dlg.getDialogPane().lookupButton(ButtonType.OK);
        ok.setText("Registrar"); ok.getStyleClass().add("btn-primary");

        dlg.setResultConverter(bt -> {
            if (bt == ButtonType.OK && !fDesc.getText().isBlank()) {
                String id = "INC-" + String.format("%03d", incidencias.size() + 1);
                Incidencia inc = new Incidencia(id, cbTipo.getValue(), fDesc.getText().trim(),
                        LocalDateTime.now(), cbEntidad.getValue(),
                        cbEvento.getValue(), cbCompra.getValue());
                incidencias.add(inc);
                ds.log("⚠ Incidencia registrada: " + id + " — " + cbTipo.getValue());
            }
            return null;
        });
        dlg.showAndWait();
    }

    private Label lbl(String t) {
        Label l = new Label(t); l.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 12px;"); return l;
    }
}