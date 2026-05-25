package co.edu.uniquindio.poo.proyectofinalprgii.UI;

import co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas.Evento;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.ServicioCorreo;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EventosUI {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd MMM yyyy  HH:mm");
    private final DataStore ds = DataStore.get();
    private TableView<Evento> table;

    public Node build() {
        VBox root = new VBox(24);
        root.setPadding(new Insets(28, 32, 32, 32));

        // ── Header + create button ────────────────────────────────
        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Gestión de Eventos");
        title.getStyleClass().add("section-title");
        title.setStyle("-fx-font-size: 20px;");
        Region sp = UtilsUI.spacer();
        Button btnNuevo = new Button("+ Nuevo Evento");
        btnNuevo.getStyleClass().add("btn-primary");
        btnNuevo.setOnAction(e -> showCreateDialog());
        Button btnEliminar = new Button("🗑 Eliminar Evento");
        btnEliminar.getStyleClass().add("btn-danger");
        btnEliminar.setOnAction(e -> {
            Evento sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) {
                new Alert(Alert.AlertType.WARNING, "Selecciona un evento de la tabla primero.").showAndWait();
                return;
            }
            Alert conf = new Alert(Alert.AlertType.CONFIRMATION,
                    "¿Eliminar \"" + sel.getNombre() + "\"? También se eliminarán sus compras.");
            conf.setHeaderText(null);
            conf.showAndWait().ifPresent(r -> {
                if (r == ButtonType.OK) {
                    var comprasAfectadas = ds.compras.stream()
                            .filter(c -> c.getEvento().getIdEvento().equals(sel.getIdEvento()))
                            .toList();
                    ServicioCorreo.notificarCancelacionEvento(sel, comprasAfectadas);
                    ds.compras.removeIf(c -> c.getEvento().getIdEvento().equals(sel.getIdEvento()));
                    ds.eventos.remove(sel);
                    ds.log("🗑 Evento eliminado: " + sel.getNombre());
                }
            });
        });
        header.getChildren().addAll(title, sp, btnNuevo, btnEliminar);

        // ── Filter bar ────────────────────────────────────────────
        HBox filters = new HBox(12);
        filters.setAlignment(Pos.CENTER_LEFT);
        TextField search = new TextField();
        search.setPromptText("🔍  Buscar evento...");
        search.setPrefWidth(260);
        ComboBox<String> catFilter = new ComboBox<>();
        catFilter.getItems().addAll("Todas las categorías", "Música", "Clásica", "Cultural", "Tech");
        catFilter.setValue("Todas las categorías");
        ComboBox<String> estadoFilter = new ComboBox<>();
        estadoFilter.getItems().addAll("Todos los estados", "PUBLICADO", "BORRADOR", "PAUSADO", "CANCELADO");
        estadoFilter.setValue("Todos los estados");
        filters.getChildren().addAll(search, catFilter, estadoFilter);

        // ── Table ─────────────────────────────────────────────────
        table = buildTable();
        VBox.setVgrow(table, Priority.ALWAYS);

        javafx.collections.transformation.FilteredList<Evento> filtrados =
                new javafx.collections.transformation.FilteredList<>(ds.eventos, p -> true);
        Runnable aplicarFiltro = () -> filtrados.setPredicate(ev -> {
            String txt = search.getText().toLowerCase().trim();
            String cat = catFilter.getValue();
            String est = estadoFilter.getValue();
            boolean okTxt = txt.isEmpty() || ev.getNombre().toLowerCase().contains(txt)
                    || ev.getCiudad().toLowerCase().contains(txt);
            boolean okCat = "Todas las categorías".equals(cat) || cat.equals(ev.getCategoria());
            boolean okEst = "Todos los estados".equals(est) || est.equals(ev.getEstado().name());
            return okTxt && okCat && okEst;
        });
        search.textProperty().addListener((o, a, b) -> aplicarFiltro.run());
        catFilter.setOnAction(e -> aplicarFiltro.run());
        estadoFilter.setOnAction(e -> aplicarFiltro.run());
        table.setItems(filtrados);

        VBox card = UtilsUI.card(filters, table);
        VBox.setVgrow(card, Priority.ALWAYS);
        root.getChildren().addAll(header, card);
        VBox.setVgrow(root, Priority.ALWAYS);
        return root;
    }

    @SuppressWarnings("unchecked")
    private TableView<Evento> buildTable() {
        TableView<Evento> tv = new TableView<>(ds.eventos);
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tv.setPrefHeight(9999);

        TableColumn<Evento, String> colId = col("ID", 90);
        colId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getIdEvento()));

        TableColumn<Evento, String> colNombre = col("Nombre", 200);
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));

        TableColumn<Evento, String> colCat = col("Categoría", 110);
        colCat.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCategoria()));
        colCat.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    return;
                }
                String color = switch (item) {
                    case "Música" -> "badge-purple";
                    case "Clásica" -> "badge-blue";
                    case "Cultural" -> "badge-green";
                    case "Tech" -> "badge-amber";
                    default -> "badge-blue";
                };
                setGraphic(UtilsUI.badge(item, color));
                setText(null);
            }
        });

        TableColumn<Evento, String> colCiudad = col("Ciudad", 110);
        colCiudad.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCiudad()));

        TableColumn<Evento, String> colFecha = col("Fecha", 160);
        colFecha.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFecha().format(FMT)));

        TableColumn<Evento, String> colRecinto = col("Recinto", 160);
        colRecinto.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRecinto().getNombre()));

        TableColumn<Evento, String> colEstado = col("Estado", 120);
        colEstado.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEstado().name()));
        colEstado.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    return;
                }
                Evento ev = getTableView().getItems().get(getIndex());
                setGraphic(UtilsUI.badgeEstadoEvento(ev.getEstado()));
                setText(null);
            }
        });

        TableColumn<Evento, Void> colAcciones = new TableColumn<>("Acciones");
        colAcciones.setPrefWidth(160);
        colAcciones.setCellFactory(col -> new TableCell<>() {
            final Button btnPub = new Button("Publicar");
            final Button btnPaus = new Button("Pausar");
            final Button btnEdit = new Button("✏");
            final Button btnAsientos = new Button("🪑");

            {
                btnPub.getStyleClass().add("btn-success");
                btnPaus.getStyleClass().add("btn-danger");
                btnPub.setStyle("-fx-font-size:11px; -fx-padding: 4 10;");
                btnPaus.setStyle("-fx-font-size:11px; -fx-padding: 4 10;");
                btnEdit.setStyle("-fx-background-color: rgba(124,58,237,0.2); -fx-text-fill: #a78bfa; " +
                        "-fx-background-radius: 6; -fx-padding: 4 8; -fx-cursor: hand; -fx-font-size:11px;");
                btnAsientos.setStyle("-fx-background-color: rgba(16,185,129,0.2); -fx-text-fill: #10b981; " +
                        "-fx-background-radius: 6; -fx-padding: 4 8; -fx-cursor: hand; -fx-font-size:11px;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }
                Evento ev = getTableView().getItems().get(getIndex());
                HBox box = new HBox(6, btnPub, btnPaus, btnEdit, btnAsientos);
                box.setAlignment(Pos.CENTER_LEFT);
                btnPub.setOnAction(e -> {
                    ev.publicar();
                    table.refresh();
                    ds.log("▶ Evento publicado: " + ev.getNombre());
                });
                btnPaus.setOnAction(e -> {
                    ev.pausar();
                    table.refresh();
                    ds.log("⏸ Evento pausado: " + ev.getNombre());
                });
                setGraphic(box);
                btnEdit.setOnAction(e -> showEditDialog(ev));
                btnAsientos.setOnAction(e -> mostrarGestionAsientos(ev));

            }
        });

        tv.getColumns().addAll(colId, colNombre, colCat, colCiudad, colFecha, colRecinto, colEstado, colAcciones);
        return tv;
    }

    private <T> TableColumn<Evento, T> col(String title, double width) {
        TableColumn<Evento, T> c = new TableColumn<>(title);
        c.setPrefWidth(width);
        return c;
    }

    // ── Create event dialog ───────────────────────────────────────
    private void showCreateDialog() {
        Dialog<Void> dlg = new Dialog<>();
        dlg.setTitle("Nuevo Evento");
        dlg.setHeaderText(null);
        dlg.getDialogPane().setStyle("-fx-background-color: #1c2128;");

        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(14);
        grid.setPadding(new Insets(20));

        TextField fNombre = field("Nombre del evento");
        TextField fDesc = field("Descripción");
        TextField fCiudad = field("Ciudad");
        ComboBox<String> fCat = new ComboBox<>();
        fCat.getItems().addAll("Música", "Clásica", "Cultural", "Tech", "Deportes");
        fCat.setValue("Música");
        DatePicker dpFecha = new DatePicker(java.time.LocalDate.now().plusDays(30));
        dpFecha.setStyle("-fx-background-color: #0d1117; -fx-text-fill: #e6edf3;");

        // PONER:
        TextField fPrecioGeneral = field("Precio General (ej: 50000)");
        TextField fPrecioPreferencial = field("Precio Preferencial (ej: 100000)");
        TextField fPrecioVIP = field("Precio VIP (ej: 200000)");
        fPrecioGeneral.setText("50000");
        fPrecioPreferencial.setText("100000");
        fPrecioVIP.setText("200000");

        grid.add(lbl("Nombre"), 0, 0);
        grid.add(fNombre, 1, 0);
        grid.add(lbl("Descripción"), 0, 1);
        grid.add(fDesc, 1, 1);
        grid.add(lbl("Ciudad"), 0, 2);
        grid.add(fCiudad, 1, 2);
        grid.add(lbl("Categoría"), 0, 3);
        grid.add(fCat, 1, 3);
        grid.add(lbl("Fecha"), 0, 4);
        grid.add(dpFecha, 1, 4);
        grid.add(lbl("Precio General"), 0, 5);
        grid.add(fPrecioGeneral, 1, 5);
        grid.add(lbl("Precio Prefer."), 0, 6);
        grid.add(fPrecioPreferencial, 1, 6);
        grid.add(lbl("Precio VIP"), 0, 7);
        grid.add(fPrecioVIP, 1, 7);

        dlg.getDialogPane().setContent(grid);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Button okBtn = (Button) dlg.getDialogPane().lookupButton(ButtonType.OK);
        okBtn.getStyleClass().add("btn-primary");
        okBtn.setText("Crear Evento");

        // PON esto:
        dlg.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                try {
                    // PONER:
                    double pGeneral = Double.parseDouble(fPrecioGeneral.getText().replaceAll("[^\\d.]", ""));
                    double pPreferencial = Double.parseDouble(fPrecioPreferencial.getText().replaceAll("[^\\d.]", ""));
                    double pVIP = Double.parseDouble(fPrecioVIP.getText().replaceAll("[^\\d.]", ""));
                    ds.crearEvento(
                            fNombre.getText().trim(),
                            fCat.getValue(),
                            fDesc.getText().trim(),
                            fCiudad.getText().trim(),
                            dpFecha.getValue().atStartOfDay(),
                            pGeneral, pPreferencial, pVIP
                    );
                } catch (Exception ex) {
                    new Alert(Alert.AlertType.ERROR, "Datos inválidos: " + ex.getMessage()).showAndWait();
                }
            }
            return null;
        });
        dlg.showAndWait();
    }

    private TextField field(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setPrefWidth(280);
        return tf;
    }

    private Label lbl(String text) {
        Label l = new Label(text);
        l.getStyleClass().add("field-label");
        l.setStyle("-fx-text-fill: #8b949e;");
        return l;
    }

    private void showEditDialog(Evento ev) {
        Dialog<Void> dlg = new Dialog<>();
        dlg.setTitle("Editar Evento");
        dlg.setHeaderText(null);
        dlg.getDialogPane().setStyle("-fx-background-color: #1c2128;");

        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(14);
        grid.setPadding(new Insets(20));

        TextField fNombre = fieldEdit(ev.getNombre());
        TextField fDesc = fieldEdit(ev.getDescripcion());
        TextField fCiudad = fieldEdit(ev.getCiudad());
        ComboBox<String> fCat = new ComboBox<>();
        fCat.getItems().addAll("Música", "Clásica", "Cultural", "Tech", "Deportes");
        fCat.setValue(ev.getCategoria());
        DatePicker dpFecha = new DatePicker(ev.getFecha().toLocalDate());

        grid.add(lbl("Nombre"), 0, 0);
        grid.add(fNombre, 1, 0);
        grid.add(lbl("Descripción"), 0, 1);
        grid.add(fDesc, 1, 1);
        grid.add(lbl("Ciudad"), 0, 2);
        grid.add(fCiudad, 1, 2);
        grid.add(lbl("Categoría"), 0, 3);
        grid.add(fCat, 1, 3);
        grid.add(lbl("Fecha"), 0, 4);
        grid.add(dpFecha, 1, 4);

        dlg.getDialogPane().setContent(grid);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Button ok = (Button) dlg.getDialogPane().lookupButton(ButtonType.OK);
        ok.setText("Guardar");
        ok.getStyleClass().add("btn-primary");

        dlg.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                ev.setNombre(fNombre.getText().trim());
                ev.setDescripcion(fDesc.getText().trim());
                ev.setCiudad(fCiudad.getText().trim());
                ev.setCategoria(fCat.getValue());
                ev.setFecha(dpFecha.getValue().atTime(ev.getFecha().toLocalTime()));
                table.refresh();
                ds.log("✏ Evento editado: " + ev.getNombre());
            }
            return null;
        });
        dlg.showAndWait();
    }

    private TextField fieldEdit(String valor) {
        TextField tf = new TextField(valor);
        tf.setPrefWidth(280);
        return tf;
    }

    private void mostrarGestionAsientos(Evento ev) {
        Dialog<Void> dlg = new Dialog<>();
        dlg.setTitle("Gestión de Asientos — " + ev.getNombre());
        dlg.setHeaderText(null);
        dlg.getDialogPane().setStyle("-fx-background-color: #1c2128; -fx-min-width: 500;");

        var asientos = ev.getRecinto().getZonas().stream()
                .flatMap(z -> z.getAsientos().stream())
                .collect(java.util.stream.Collectors.toList());

        javafx.collections.ObservableList<co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas.Asiento> lista =
                javafx.collections.FXCollections.observableArrayList(asientos);

        TableView<co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas.Asiento> tv = new TableView<>(lista);
        tv.setPrefHeight(350);
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas.Asiento, String> cId = new TableColumn<>("ID");
        cId.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getIdAsiento()));

        TableColumn<co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas.Asiento, String> cEstado = new TableColumn<>("Estado");
        cEstado.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getEstado().name()));
        cEstado.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                    return;
                }
                setText(item);
                String style;
                if ("DISPONIBLE".equals(item)) {
                    style = "-fx-text-fill: #10b981; -fx-font-weight: bold;";
                } else if ("VENDIDO".equals(item)) {
                    style = "-fx-text-fill: #ef4444; -fx-font-weight: bold;";
                } else if ("RESERVADO".equals(item)) {
                    style = "-fx-text-fill: #f59e0b; -fx-font-weight: bold;";
                } else if ("BLOQUEADO".equals(item)) {
                    style = "-fx-text-fill: #8b949e; -fx-font-weight: bold;";
                } else {
                    style = "-fx-text-fill: #e6edf3;";
                }
                setStyle(style);
            }
        });

        TableColumn<co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas.Asiento, String> cTipo = new TableColumn<>("Tipo");
        cTipo.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTipoZona()));

        TableColumn<co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas.Asiento, Void> cAcc = new TableColumn<>("Acción");
        cAcc.setCellFactory(col -> new TableCell<>() {
            final Button btnLib = new Button("✅ Habilitar");
            final Button btnBlq = new Button("🚫 Bloquear");

            {
                btnLib.setStyle("-fx-font-size:10px; -fx-padding: 3 8;");
                btnBlq.setStyle("-fx-font-size:10px; -fx-padding: 3 8;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }
                var a = getTableView().getItems().get(getIndex());
                btnLib.setOnAction(e -> {
                    a.cambiarEstado(co.edu.uniquindio.poo.proyectofinalprgii.Class.Enums.EstadoAsiento.DISPONIBLE);
                    tv.getItems().setAll(ev.getRecinto().getZonas().stream()
                            .flatMap(z -> z.getAsientos().stream())
                            .collect(java.util.stream.Collectors.toList()));
                });
                btnBlq.setOnAction(e -> {
                    a.cambiarEstado(co.edu.uniquindio.poo.proyectofinalprgii.Class.Enums.EstadoAsiento.BLOQUEADO);
                    tv.getItems().setAll(ev.getRecinto().getZonas().stream()
                            .flatMap(z -> z.getAsientos().stream())
                            .collect(java.util.stream.Collectors.toList()));
                });
                setGraphic(new HBox(6, btnLib, btnBlq));
            }
        });

        tv.getColumns().addAll(cId, cTipo, cEstado, cAcc);
        dlg.getDialogPane().setContent(tv);
        dlg.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dlg.showAndWait();
    }
}