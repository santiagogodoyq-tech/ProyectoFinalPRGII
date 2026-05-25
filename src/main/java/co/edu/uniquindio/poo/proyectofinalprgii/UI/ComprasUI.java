package co.edu.uniquindio.poo.proyectofinalprgii.UI;

import co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas.*;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.Comportamental.State.StateCompra;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.Comportamental.Strategy.CancelacionAnticipadaStrategy;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.Enums.EstadoCompra;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.MetodoPago;
import co.edu.uniquindio.poo.proyectofinalprgii.UI.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.format.DateTimeFormatter;

public class ComprasUI {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final DataStore ds = DataStore.get();
    private TableView<Compra> table;

    public Node build() {
        VBox root = new VBox(24);
        root.setPadding(new Insets(28, 32, 32, 32));

        // ── Header ────────────────────────────────────────────────
        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Gestión de Compras");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: -text-primary;");
        Region sp = UtilsUI.spacer();
        Button btnNueva = new Button("+ Nueva Compra");
        btnNueva.getStyleClass().add("btn-primary");
        btnNueva.setOnAction(e -> showCreateDialog());
        header.getChildren().addAll(title, sp, btnNueva);

        // ── Summary strip ─────────────────────────────────────────
        HBox strip = new HBox(12);
        strip.getChildren().addAll(
                miniStat("Total compras",  String.valueOf(ds.compras.size()), "#a78bfa"),
                miniStat("Ingresos",       UtilsUI.currency(ds.totalIngresos()), "#10b981"),
                miniStat("Activas",        String.valueOf(ds.comprasActivas()), "#3b82f6"),
                miniStat("Canceladas",     String.valueOf(ds.compras.stream().filter(c -> c.getEstado() == EstadoCompra.CANCELADA).count()), "#ef4444")
        );

        // ── Table ─────────────────────────────────────────────────
        table = buildTable();
        VBox.setVgrow(table, Priority.ALWAYS);
        VBox card = UtilsUI.card(table);
        VBox.setVgrow(card, Priority.ALWAYS);

        root.getChildren().addAll(header, strip, card);
        VBox.setVgrow(root, Priority.ALWAYS);
        return root;
    }

    private HBox miniStat(String label, String value, String color) {
        VBox box = new VBox(4);
        box.setStyle("-fx-background-color: -bg-card; -fx-background-radius: 10; " +
                "-fx-border-color: -border; -fx-border-radius: 10; -fx-padding: 14 20;");
        HBox.setHgrow(box, Priority.ALWAYS);
        Label v = new Label(value);
        v.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        Label l = new Label(label);
        l.setStyle("-fx-font-size: 11px; -fx-text-fill: -text-muted;");
        box.getChildren().addAll(v, l);
        return new HBox(box) {{ HBox.setHgrow(this, Priority.ALWAYS); }};
    }

    @SuppressWarnings("unchecked")
    private TableView<Compra> buildTable() {
        TableView<Compra> tv = new TableView<>(ds.compras);
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tv.setPrefHeight(9999);

        TableColumn<Compra, String> colId = colC("ID Compra", 120);
        colId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getIdCompra()));

        TableColumn<Compra, String> colUsuario = colC("Usuario", 150);
        colUsuario.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUsuario().getNombre()));
        colUsuario.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                HBox row = new HBox(8);
                row.setAlignment(Pos.CENTER_LEFT);
                Label av = new Label(item.substring(0, 1));
                av.setStyle("-fx-background-color: -accent; -fx-background-radius: 14; " +
                        "-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px; " +
                        "-fx-min-width: 26; -fx-min-height: 26; -fx-alignment: center;");
                Label nm = new Label(item);
                nm.setStyle("-fx-text-fill: -text-primary;");
                row.getChildren().addAll(av, nm);
                setGraphic(row); setText(null);
            }
        });

        TableColumn<Compra, String> colEvento = colC("Evento", 180);
        colEvento.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEvento().getNombre()));

        TableColumn<Compra, String> colFecha = colC("Fecha", 130);
        colFecha.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFecha().format(FMT)));

        TableColumn<Compra, String> colTotal = colC("Total", 110);
        colTotal.setCellValueFactory(c -> new SimpleStringProperty(UtilsUI.currency(c.getValue().getTotal())));
        colTotal.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); return; }
                setText(item);
                setStyle("-fx-font-weight: bold; -fx-text-fill: #10b981;");
            }
        });

        TableColumn<Compra, String> colEstado = colC("Estado", 120);
        colEstado.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEstado().name()));
        colEstado.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                Compra comp = getTableView().getItems().get(getIndex());
                setGraphic(UtilsUI.badgeEstadoCompra(comp.getEstado()));
                setText(null);
            }
        });

        TableColumn<Compra, Void> colOps = new TableColumn<>("Operaciones");
        colOps.setPrefWidth(220);
        colOps.setCellFactory(col -> new TableCell<>() {
            final Button btnPagar     = new Button("Pagar");
            final Button btnCancelar  = new Button("Cancelar");
            final Button btnQR        = new Button("QR");

            {
                btnPagar.getStyleClass().add("btn-success");
                btnCancelar.getStyleClass().add("btn-danger");
                btnQR.getStyleClass().add("btn-ghost");
                String sm = "-fx-font-size:11px; -fx-padding: 4 10;";
                btnPagar.setStyle(sm); btnCancelar.setStyle(sm); btnQR.setStyle(sm);
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                Compra comp = getTableView().getItems().get(getIndex());
                HBox box = new HBox(6);
                box.setAlignment(Pos.CENTER_LEFT);

                btnPagar.setOnAction(e -> {
                    comp.setEstrategiaPago(new co.edu.uniquindio.poo.proyectofinalprgii.Class.Comportamental.Strategy.PagoTarjetaStrategy());
                    comp.pagar(null);
                    table.refresh();
                    ds.log("💳 Pago procesado: " + comp.getIdCompra());
                });
                btnCancelar.setOnAction(e -> {
                    comp.setEstrategiaCancelacion(new CancelacionAnticipadaStrategy());
                    comp.cancelar();
                    table.refresh();
                    ds.log("❌ Compra cancelada: " + comp.getIdCompra());
                });
                btnQR.setOnAction(e -> {
                    if (!comp.getEntradas().isEmpty()) {
                        String qr = comp.getEntradas().get(0).generarQR();
                        showQRDialog(qr, comp);
                    }
                });

                EstadoCompra est = comp.getEstado();
                btnPagar.setDisable(est != EstadoCompra.CREADA);
                btnCancelar.setDisable(est == EstadoCompra.CANCELADA || est == EstadoCompra.REEMBOLSADA);
                btnQR.setDisable(est == EstadoCompra.CANCELADA);

                box.getChildren().addAll(btnPagar, btnCancelar, btnQR);
                setGraphic(box);
            }
        });

        tv.getColumns().addAll(colId, colUsuario, colEvento, colFecha, colTotal, colEstado, colOps);
        return tv;
    }

    private <T> TableColumn<Compra, T> colC(String title, double width) {
        TableColumn<Compra, T> c = new TableColumn<>(title);
        c.setPrefWidth(width);
        return c;
    }

    // ── QR Dialog ─────────────────────────────────────────────────
    private void showQRDialog(String qrCode, Compra compra) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Código QR de Entrada");
        a.setHeaderText(null);
        VBox content = new VBox(12);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(16));

        Label title = new Label("🎫  Entrada Generada");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #a78bfa;");

        // Fake QR grid
        Label qr = new Label(buildFakeQR());
        qr.setStyle("-fx-font-family: monospace; -fx-font-size: 8px; " +
                "-fx-background-color: white; -fx-text-fill: black; -fx-padding: 10;");

        Label code = new Label(qrCode);
        code.setStyle("-fx-font-family: monospace; -fx-font-size: 12px; " +
                "-fx-text-fill: #a78bfa; -fx-font-weight: bold;");

        Label info = new Label("Evento: " + compra.getEvento().getNombre() + "\n" +
                "Usuario: " + compra.getUsuario().getNombre() + "\n" +
                "Total: " + UtilsUI.currency(compra.getTotal()));
        info.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 12px;");
        info.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        content.getChildren().addAll(title, qr, code, info);
        a.getDialogPane().setContent(content);
        a.getDialogPane().setStyle("-fx-background-color: #1c2128;");
        a.showAndWait();
    }

    private String buildFakeQR() {
        String[] rows = {
                "██████ ██ ██████",
                "█    █ ██ █    █",
                "█ ██ █  █ █ ██ █",
                "█    █ ██ █    █",
                "██████ ██ ██████",
                "  ██ ██ ██ ██  ",
                "██ ██  ██  ██ ██",
                "  ██ ██ ██ ██  ",
                "██████ ██ ██████",
                "█    █ ██ █    █",
                "█ ██ █  █ █ ██ █",
                "█    █ ██ █    █",
                "██████ ██ ██████"
        };
        return String.join("\n", rows);
    }

    // ── Create purchase dialog ────────────────────────────────────
    private void showCreateDialog() {
        Dialog<Void> dlg = new Dialog<>();
        dlg.setTitle("Nueva Compra");
        dlg.setHeaderText(null);
        dlg.getDialogPane().setStyle("-fx-background-color: #1c2128;");

        GridPane grid = new GridPane();
        grid.setHgap(16); grid.setVgap(14);
        grid.setPadding(new Insets(20));

        ComboBox<UsuarioFinal> cbUsuario = new ComboBox<>();
        cbUsuario.getItems().addAll(ds.usuarios);
        cbUsuario.setPromptText("Seleccionar usuario");
        cbUsuario.setConverter(new javafx.util.StringConverter<>() {
            public String toString(UsuarioFinal u) { return u == null ? "" : u.getNombre(); }
            public UsuarioFinal fromString(String s) { return null; }
        });

        ComboBox<Evento> cbEvento = new ComboBox<>();
        cbEvento.getItems().addAll(ds.eventos);
        cbEvento.setPromptText("Seleccionar evento");
        cbEvento.setConverter(new javafx.util.StringConverter<>() {
            public String toString(Evento e) { return e == null ? "" : e.getNombre(); }
            public Evento fromString(String s) { return null; }
        });

        ComboBox<String> cbTipo = new ComboBox<>();
        cbTipo.getItems().addAll("General", "VIP", "Preferencial");
        cbTipo.setValue("General");

        ComboBox<Asiento> cbAsiento = new ComboBox<>();
        cbAsiento.setPromptText("Seleccionar asiento");
        cbAsiento.setConverter(new javafx.util.StringConverter<>() {
            public String toString(Asiento a) { return a == null ? "" : a.getIdAsiento(); }
            public Asiento fromString(String s) { return null; }
        });

        ComboBox<String> cbMetodoPago = new ComboBox<>();
        cbMetodoPago.getItems().addAll("Tarjeta", "PSE", "Efectivo");
        cbMetodoPago.setValue("Tarjeta");

        // Al cambiar evento o tipo, actualizar asientos disponibles
        Runnable actualizarAsientos = () -> {
            Evento ev = cbEvento.getValue();
            String tipo = cbTipo.getValue();
            cbAsiento.getItems().clear();
            if (ev == null) return;
            String tipoInterno = switch (tipo) {
                case "VIP"         -> "VIP";
                case "Preferencial"-> "PREFERENCIAL";
                default            -> "GENERAL";
            };
            ev.getRecinto().getZonas().stream()
                    .flatMap(z -> z.getAsientos().stream())
                    .filter(a -> a.getTipoZona().equalsIgnoreCase(tipoInterno))
                    .filter(a -> a.getEstado() == co.edu.uniquindio.poo.proyectofinalprgii.Class.Enums.EstadoAsiento.DISPONIBLE)
                    .forEach(cbAsiento.getItems()::add);
        };

        cbEvento.setOnAction(e -> actualizarAsientos.run());
        cbTipo.setOnAction(e -> actualizarAsientos.run());

        grid.add(lbl("Usuario"),      0, 0); grid.add(cbUsuario,    1, 0);
        grid.add(lbl("Evento"),       0, 1); grid.add(cbEvento,     1, 1);
        grid.add(lbl("Tipo entrada"), 0, 2); grid.add(cbTipo,       1, 2);
        grid.add(lbl("Asiento"),      0, 3); grid.add(cbAsiento,    1, 3);
        grid.add(lbl("Método pago"),  0, 4); grid.add(cbMetodoPago, 1, 4);

        dlg.getDialogPane().setContent(grid);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Button okBtn = (Button) dlg.getDialogPane().lookupButton(ButtonType.OK);
        okBtn.getStyleClass().add("btn-primary");
        okBtn.setText("Crear Compra");

        dlg.setResultConverter(bt -> {
            if (bt == ButtonType.OK
                    && cbUsuario.getValue() != null
                    && cbEvento.getValue()  != null
                    && cbAsiento.getValue() != null) {

                co.edu.uniquindio.poo.proyectofinalprgii.Class.Comportamental.Strategy.PagoStrategy estrategia =
                        switch (cbMetodoPago.getValue()) {
                            case "PSE"      -> new co.edu.uniquindio.poo.proyectofinalprgii.Class.Comportamental.Strategy.PagoPSEStrategy();
                            case "Efectivo" -> new co.edu.uniquindio.poo.proyectofinalprgii.Class.Comportamental.Strategy.PagoEfectivoStrategy();
                            default         -> new co.edu.uniquindio.poo.proyectofinalprgii.Class.Comportamental.Strategy.PagoTarjetaStrategy();
                        };

                UsuarioFinal u = cbUsuario.getValue();
                MetodoPago mp = u.getMetodosPago().isEmpty()
                        ? new MetodoPago("MP-TMP", cbMetodoPago.getValue().toUpperCase(), cbMetodoPago.getValue())
                        : u.getMetodosPago().get(0);

                ds.nuevaCompra(u, cbEvento.getValue(), cbAsiento.getValue(), estrategia, mp);
                table.refresh();
            }
            return null;
        });
        dlg.showAndWait();
    }

    private Label lbl(String t) {
        Label l = new Label(t);
        l.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 12px;");
        return l;
    }

}
