package co.edu.uniquindio.poo.proyectofinalprgii.UI;

import co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas.UsuarioFinal;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.MetodoPago;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class UsuarioUI {
    private final DataStore ds = DataStore.get();

    public Node build() {
        VBox root = new VBox(24);
        root.setPadding(new Insets(28, 32, 32, 32));

        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Gestión de Usuarios");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: -text-primary;");
        Region sp = UtilsUI.spacer();
        Button btnNuevo = new Button("+ Nuevo Usuario");
        btnNuevo.getStyleClass().add("btn-primary");
        Button btnElim = new Button("🗑 Eliminar Usuario");
        btnElim.getStyleClass().add("btn-primary");
        btnNuevo.setOnAction(e -> showCreateDialog());
        btnElim.setOnAction(e -> showDelateDialog());
        header.getChildren().addAll(title, sp, btnNuevo, btnElim);

        // PONER:
        TextField fBuscar = new TextField();
        fBuscar.setPromptText("🔍  Buscar por nombre o correo...");
        fBuscar.setPrefWidth(280);

        TableView<UsuarioFinal> tv = buildTable();
        VBox.setVgrow(tv, Priority.ALWAYS);

        javafx.collections.transformation.FilteredList<UsuarioFinal> filtrados =
                new javafx.collections.transformation.FilteredList<>(ds.usuarios, p -> true);
        fBuscar.textProperty().addListener((o, a, b) ->
                filtrados.setPredicate(u -> b.isBlank()
                        || u.getNombre().toLowerCase().contains(b.toLowerCase())
                        || u.getCorreo().toLowerCase().contains(b.toLowerCase())));
        tv.setItems(filtrados);

        HBox filterBar = new HBox(fBuscar);
        VBox card = UtilsUI.card(filterBar, tv);
        VBox.setVgrow(card, Priority.ALWAYS);

        root.getChildren().addAll(header, card);
        VBox.setVgrow(root, Priority.ALWAYS);
        return root;
    }

    @SuppressWarnings("unchecked")
    private TableView<UsuarioFinal> buildTable() {
        TableView<UsuarioFinal> tv = new TableView<>(ds.usuarios);
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tv.setPrefHeight(9999);

        TableColumn<UsuarioFinal, String> colAvatar = new TableColumn<>("");
        colAvatar.setPrefWidth(52);
        colAvatar.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                UsuarioFinal u = getTableView().getItems().get(getIndex());
                Label av = new Label(u.getNombre().substring(0, 1).toUpperCase());
                av.setStyle("-fx-background-color: -accent; -fx-background-radius: 20; " +
                        "-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; " +
                        "-fx-min-width: 36; -fx-min-height: 36; -fx-alignment: center;");
                setGraphic(av); setText(null);
            }
        });

        TableColumn<UsuarioFinal, String> colNombre = col("Nombre", 180);
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));

        TableColumn<UsuarioFinal, String> colCorreo = col("Correo", 200);
        colCorreo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCorreo()));

        TableColumn<UsuarioFinal, String> colTel = col("Teléfono", 140);
        colTel.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTelefono()));

        TableColumn<UsuarioFinal, String> colMetodos = col("Métodos de Pago", 160);
        colMetodos.setCellValueFactory(c -> {
            long cnt = c.getValue().getMetodosPago().size();
            String tipos = c.getValue().getMetodosPago().stream()
                    .map(MetodoPago::getTipo)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("—");
            return new SimpleStringProperty(cnt + " método(s): " + tipos);
        });

        TableColumn<UsuarioFinal, String> colCompras = col("Compras", 100);
        colCompras.setCellValueFactory(c -> {
            long cnt = ds.compras.stream()
                    .filter(comp -> comp.getUsuario().getIdUsuario().equals(c.getValue().getIdUsuario()))
                    .count();
            return new SimpleStringProperty(String.valueOf(cnt));
        });

        TableColumn<UsuarioFinal, String> colGasto = col("Total gastado", 130);
        colGasto.setCellValueFactory(c -> {
            double total = ds.compras.stream()
                    .filter(comp -> comp.getUsuario().getIdUsuario().equals(c.getValue().getIdUsuario()))
                    .mapToDouble(co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas.Compra::getTotal)
                    .sum();
            return new SimpleStringProperty(UtilsUI.currency(total));
        });
        colGasto.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); return; }
                setText(item);
                setStyle("-fx-font-weight: bold; -fx-text-fill: #10b981;");
            }
        });

        tv.getColumns().addAll(colAvatar, colNombre, colCorreo, colTel, colMetodos, colCompras, colGasto);
        return tv;
    }

    private <T> TableColumn<UsuarioFinal, T> col(String title, double width) {
        TableColumn<UsuarioFinal, T> c = new TableColumn<>(title);
        c.setPrefWidth(width);
        return c;
    }

    private void showCreateDialog() {
        Dialog<Void> dlg = new Dialog<>();
        dlg.setTitle("Nuevo Usuario");
        dlg.setHeaderText(null);
        dlg.getDialogPane().setStyle("-fx-background-color: #1c2128;");

        GridPane grid = new GridPane();
        grid.setHgap(16); grid.setVgap(14);
        grid.setPadding(new Insets(20));

        TextField fNombre = field("Nombre completo");
        TextField fCorreo = field("correo@ejemplo.com");
        TextField fCon    = field("Example: 54536S!");
        TextField fTel    = field("310-000-0000");

        ComboBox<String> fMetodo = new ComboBox<>();
        fMetodo.getItems().addAll("TARJETA_CREDITO", "PSE", "EFECTIVO");
        fMetodo.setValue("PSE");

        grid.add(lbl("Nombre"),   0, 0); grid.add(fNombre, 1, 0);
        grid.add(lbl("Correo"),   0, 1); grid.add(fCorreo, 1, 1);
        grid.add(lbl("Teléfono"), 0, 2); grid.add(fTel,    1, 2);
        grid.add(lbl("Método"),   0, 3); grid.add(fMetodo, 1, 3);

        dlg.getDialogPane().setContent(grid);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Button okBtn = (Button) dlg.getDialogPane().lookupButton(ButtonType.OK);
        okBtn.getStyleClass().add("btn-primary");
        okBtn.setText("Crear Usuario");

        dlg.setResultConverter(bt -> {
            if (bt == ButtonType.OK && !fNombre.getText().isBlank()) {
                String id = "U-" + String.format("%03d", ds.usuarios.size() + 1);
                UsuarioFinal u = new UsuarioFinal(id, fNombre.getText().trim(),
                        fCorreo.getText().trim(), fTel.getText().trim(), fCon.getText().trim());
                MetodoPago mp = new MetodoPago("MP-NEW", fMetodo.getValue(), "—");
                u.agregarMetodoPago(mp);
                ds.usuarios.add(u);
                ds.log("👤 Usuario creado: " + u.getNombre());
            }
            return null;
        });
        dlg.showAndWait();
    }

    private void showDelateDialog() {
        Dialog<Void> dlg = new Dialog<>();
        dlg.setTitle("Eliminar Usuario");
        dlg.setHeaderText(null);
        dlg.getDialogPane().setStyle("-fx-background-color: #1c2128;");

        GridPane grid = new GridPane();
        grid.setHgap(16); grid.setVgap(14);
        grid.setPadding(new Insets(20));

        // ComboBox con los usuarios actuales
        ComboBox<UsuarioFinal> cbUsuario = new ComboBox<>(ds.usuarios);
        cbUsuario.setPromptText("Seleccionar usuario...");
        cbUsuario.setPrefWidth(260);

        // Mostrar nombre + correo en el combo
        cbUsuario.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(UsuarioFinal u, boolean empty) {
                super.updateItem(u, empty);
                setText(empty || u == null ? null : u.getNombre() + " — " + u.getCorreo());
            }
        });
        cbUsuario.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(UsuarioFinal u, boolean empty) {
                super.updateItem(u, empty);
                setText(empty || u == null ? null : u.getNombre() + " — " + u.getCorreo());
            }
        });

        // Labels de previsualización
        Label lblInfo = new Label("Selecciona un usuario para ver sus datos");
        lblInfo.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 12px;");
        Label lblAdvertencia = new Label("⚠ También se eliminarán todas sus compras.");
        lblAdvertencia.setStyle("-fx-text-fill: #f59e0b; -fx-font-size: 11px;");
        lblAdvertencia.setVisible(false);

        cbUsuario.setOnAction(e -> {
            UsuarioFinal sel = cbUsuario.getValue();
            if (sel != null) {
                long comprasDelUsuario = ds.compras.stream()
                        .filter(c -> c.getUsuario().getIdUsuario().equals(sel.getIdUsuario()))
                        .count();
                lblInfo.setText("Tel: " + sel.getTelefono() + "  |  Compras: " + comprasDelUsuario);
                lblAdvertencia.setVisible(comprasDelUsuario > 0);
            }
        });

        grid.add(lbl("Usuario"),    0, 0); grid.add(cbUsuario,     1, 0);
        grid.add(lbl("Info"),       0, 1); grid.add(lblInfo,       1, 1);
        grid.add(new Label(),       0, 2); grid.add(lblAdvertencia, 1, 2);

        dlg.getDialogPane().setContent(grid);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Button okBtn = (Button) dlg.getDialogPane().lookupButton(ButtonType.OK);
        okBtn.getStyleClass().add("btn-primary");
        okBtn.setText("Eliminar Usuario");
        // Deshabilitar OK hasta que se seleccione alguien
        okBtn.setDisable(true);
        cbUsuario.setOnAction(e -> {
            UsuarioFinal sel = cbUsuario.getValue();
            okBtn.setDisable(sel == null);
            if (sel != null) {
                long cnt = ds.compras.stream()
                        .filter(c -> c.getUsuario().getIdUsuario().equals(sel.getIdUsuario()))
                        .count();
                lblInfo.setText("Tel: " + sel.getTelefono() + "  |  Compras: " + cnt);
                lblAdvertencia.setVisible(cnt > 0);
            }
        });

        dlg.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                UsuarioFinal sel = cbUsuario.getValue();
                if (sel != null) {
                    ds.eliminarUsuario(sel.getIdUsuario());
                }
            }
            return null;
        });
        dlg.showAndWait();
    }

    private TextField field(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setPrefWidth(260);
        return tf;
    }

    private Label lbl(String t) {
        Label l = new Label(t);
        l.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 12px;");
        return l;
    }
}
