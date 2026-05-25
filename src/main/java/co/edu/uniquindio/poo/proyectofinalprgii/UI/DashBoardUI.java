package co.edu.uniquindio.poo.proyectofinalprgii.UI;

import co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas.Compra;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas.Evento;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

import java.time.format.DateTimeFormatter;

public class DashBoardUI {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private final DataStore ds = DataStore.get();

    public Node build() {
        ScrollPane scroll = new ScrollPane(content());
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("scroll-pane");
        return scroll;
    }

    private VBox content() {
        VBox root = new VBox(24);
        root.setPadding(new Insets(28, 32, 32, 32));

        // ── Stats row ────────────────────────────────────────────
        HBox stats = new HBox(16);
        stats.getChildren().addAll(
                growCard(UtilsUI.statCard(String.valueOf(ds.totalEventos()),  "Eventos activos",    "+" + ds.totalEventos() + " este mes",   true)),
                growCard(UtilsUI.statCard(String.valueOf(ds.totalCompras()),  "Compras totales",    "+" + ds.totalCompras() + " esta semana", true)),
                growCard(UtilsUI.statCard(UtilsUI.currency(ds.totalIngresos()), "Ingresos totales", "+12.4% vs mes ant.",                   true)),
                growCard(UtilsUI.statCard(String.valueOf(ds.comprasActivas()),"Compras activas",    ds.comprasActivas() + " confirmadas",    true))
        );

        // ── Two-column middle row ─────────────────────────────────
        HBox middle = new HBox(16);

        // Left: próximos eventos
        VBox eventosCard = UtilsUI.card(
                UtilsUI.sectionHeader("Próximos Eventos", "Los más cercanos"),
                eventosTable()
        );
        HBox.setHgrow(eventosCard, Priority.ALWAYS);

        // Right: últimas compras
        VBox comprasCard = UtilsUI.card(
                UtilsUI.sectionHeader("Últimas Compras", "Actividad reciente"),
                comprasList()
        );
        comprasCard.setMinWidth(320);
        comprasCard.setMaxWidth(360);

        middle.getChildren().addAll(eventosCard, comprasCard);

        // ── Activity log ──────────────────────────────────────────
        VBox logCard = UtilsUI.card(
                UtilsUI.sectionHeader("Log de Actividad", "Eventos del sistema en tiempo real"),
                buildLog()
        );

        root.getChildren().addAll(stats, middle, logCard);
        return root;
    }

    private VBox growCard(VBox inner) {
        HBox.setHgrow(inner, Priority.ALWAYS);
        inner.setMaxWidth(Double.MAX_VALUE);
        return inner;
    }

    // ── Próximos eventos mini-table ──────────────────────────────
    private VBox eventosTable() {
        VBox list = new VBox(0);
        int max = Math.min(ds.eventos.size(), 5);
        for (int i = 0; i < max; i++) {
            Evento ev = ds.eventos.get(i);
            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(10, 0, 10, 0));

            // Color dot by category
            String dotColor = switch (ev.getCategoria()) {
                case "Música"   -> "#a78bfa";
                case "Clásica"  -> "#3b82f6";
                case "Cultural" -> "#10b981";
                case "Tech"     -> "#f59e0b";
                default         -> "#8b949e";
            };
            Label dot = UtilsUI.dot(dotColor);

            VBox info = new VBox(2);
            Label nombre = new Label(ev.getNombre());
            nombre.setStyle("-fx-text-fill: -text-primary; -fx-font-size: 13px;");
            Label ciudad = new Label(ev.getCiudad() + "  ·  " + ev.getFecha().format(FMT));
            ciudad.setStyle("-fx-text-fill: -text-muted; -fx-font-size: 11px;");
            info.getChildren().addAll(nombre, ciudad);

            Region sp = UtilsUI.spacer();
            Label badge = UtilsUI.badgeEstadoEvento(ev.getEstado());

            row.getChildren().addAll(dot, info, sp, badge);

            if (i < max - 1) {
                Separator sep = new Separator();
                list.getChildren().addAll(row, sep);
            } else {
                list.getChildren().add(row);
            }
        }
        return list;
    }

    // ── Últimas compras mini list ────────────────────────────────
    private VBox comprasList() {
        VBox list = new VBox(8);
        int max = Math.min(ds.compras.size(), 5);
        for (int i = 0; i < max; i++) {
            Compra c = ds.compras.get(i);
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(8, 10, 8, 10));
            row.setStyle("-fx-background-color: -bg-hover; -fx-background-radius: 8;");

            // Avatar circle
            Label avatar = new Label(c.getUsuario().getNombre().substring(0, 1).toUpperCase());
            avatar.setStyle("-fx-background-color: -accent; -fx-background-radius: 20; " +
                    "-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px; " +
                    "-fx-min-width: 32; -fx-min-height: 32; -fx-alignment: center;");

            VBox info = new VBox(2);
            Label user = new Label(c.getUsuario().getNombre());
            user.setStyle("-fx-text-fill: -text-primary; -fx-font-size: 12px; -fx-font-weight: bold;");
            Label evento = new Label(c.getEvento().getNombre());
            evento.setStyle("-fx-text-fill: -text-muted; -fx-font-size: 10px;");
            info.getChildren().addAll(user, evento);
            HBox.setHgrow(info, Priority.ALWAYS);

            VBox right = new VBox(2);
            right.setAlignment(Pos.CENTER_RIGHT);
            Label monto = new Label(UtilsUI.currency(c.getTotal()));
            monto.setStyle("-fx-text-fill: -text-primary; -fx-font-size: 12px; -fx-font-weight: bold;");
            Label badge = UtilsUI.badgeEstadoCompra(c.getEstado());
            right.getChildren().addAll(monto, badge);

            row.getChildren().addAll(avatar, info, right);
            list.getChildren().add(row);
        }
        return list;
    }

    // ── Activity log list ────────────────────────────────────────
    private ListView<String> buildLog() {
        ListView<String> lv = new ListView<>(ds.logLines);
        lv.setPrefHeight(150);
        lv.setStyle("-fx-background-color: -bg-deep; -fx-border-color: -border; " +
                "-fx-border-radius: 8; -fx-background-radius: 8;");
        lv.setCellFactory(v -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setGraphic(null); return; }
                Label l = new Label(item);
                l.setStyle("-fx-text-fill: -text-muted; -fx-font-size: 11px; -fx-font-family: monospace;");
                setGraphic(l);
                setStyle("-fx-background-color: transparent; -fx-padding: 3 8;");
            }
        });
        return lv;
    }
}