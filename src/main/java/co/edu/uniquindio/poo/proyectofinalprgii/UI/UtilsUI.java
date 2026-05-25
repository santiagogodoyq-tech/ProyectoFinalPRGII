package co.edu.uniquindio.poo.proyectofinalprgii.UI;

import co.edu.uniquindio.poo.proyectofinalprgii.Class.Enums.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
public class UtilsUI {

    /** Stat card: value + label + optional delta */
    public static VBox statCard(String value, String label, String delta, boolean positive) {
        VBox box = new VBox(4);
        box.getStyleClass().add("card-stat");

        Label valLbl = new Label(value);
        valLbl.getStyleClass().add("stat-value");

        Label labLbl = new Label(label);
        labLbl.getStyleClass().add("stat-label");

        box.getChildren().addAll(valLbl, labLbl);

        if (delta != null) {
            Label dLbl = new Label(delta);
            dLbl.getStyleClass().add(positive ? "stat-delta-pos" : "stat-delta-neg");
            box.getChildren().add(dLbl);
        }
        return box;
    }

    /** Badge chip */
    public static Label badge(String text, String cssClass) {
        Label l = new Label(text);
        l.getStyleClass().add(cssClass);
        return l;
    }

    /** Badge by EstadoCompra */
    public static Label badgeEstadoCompra(EstadoCompra estado) {
        String css = switch (estado) {
            case PAGADA, CONFIRMADA -> "badge-green";
            case CREADA             -> "badge-amber";
            case CANCELADA,
                 REEMBOLSADA       -> "badge-red";
            case INCIDENCIA         -> "badge-blue";
        };
        return badge(estado.name(), css);
    }

    /** Badge by EstadoEvento */
    public static Label badgeEstadoEvento(EstadoEvento estado) {
        String css = switch (estado) {
            case PUBLICADO  -> "badge-green";
            case BORRADOR   -> "badge-amber";
            case PAUSADO    -> "badge-blue";
            case CANCELADO,
                 FINALIZADO -> "badge-red";
        };
        return badge(estado.name(), css);
    }

    /** Section title row */
    public static HBox sectionHeader(String title, String sub) {
        Label t = new Label(title);
        t.getStyleClass().add("section-title");
        Label s = new Label(sub);
        s.getStyleClass().add("section-sub");
        VBox txt = new VBox(2, t, s);
        HBox row = new HBox(txt);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    /** Thin horizontal separator */
    public static Separator sep() {
        Separator s = new Separator();
        VBox.setMargin(s, new Insets(4, 0, 4, 0));
        return s;
    }

    /** Generic card wrapper */
    public static VBox card(javafx.scene.Node... children) {
        VBox box = new VBox(14);
        box.getStyleClass().add("card");
        box.getChildren().addAll(children);
        return box;
    }

    /** Row spacer (grows) */
    public static Region spacer() {
        Region r = new Region();
        HBox.setHgrow(r, Priority.ALWAYS);
        return r;
    }

    /** Colored dot */
    public static Label dot(String color) {
        Label l = new Label("●");
        l.setTextFill(Color.web(color));
        l.setStyle("-fx-font-size: 10px;");
        return l;
    }

    /** Format currency */
    public static String currency(double v) {
        return String.format("$%,.0f", v);
    }
}
