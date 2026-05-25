package co.edu.uniquindio.poo.proyectofinalprgii.Application;

import co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas.Administrador;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas.Usuario;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas.UsuarioFinal;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.Estructural.Proxy.ProxyAcceso;
import co.edu.uniquindio.poo.proyectofinalprgii.UI.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class FXApplication extends Application {

    private Stage primaryStage;
    private Scene scene;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;

        // Arrancar siempre en el Login
        scene = new Scene((Parent) buildLogin(), 1280, 800);
        scene.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource(
                                "/co/edu/uniquindio/poo/proyectofinalprgii/Styles.css"))
                        .toExternalForm()
        );

        stage.setTitle("EventosPro — Inicio de Sesión");
        stage.setScene(scene);
        stage.setMinWidth(960);
        stage.setMinHeight(640);
        stage.show();
    }

    // ── Login ─────────────────────────────────────────────────────
    private Node buildLogin() {
        return new LoginUI(this::onLoginExitoso).build();
    }

    private void onLoginExitoso(Usuario usuario) {
        if (usuario instanceof Administrador) {
            primaryStage.setTitle("EventosPro — Panel Administrador");
            scene.setRoot((Parent) buildAdminApp());
        } else if (usuario instanceof UsuarioFinal uf) {
            primaryStage.setTitle("EventosPro — " + uf.getNombre());
            scene.setRoot((Parent) new UsuarioAppUI(uf, this::volverAlLogin).build());
        }
    }

    private void volverAlLogin() {
        primaryStage.setTitle("EventosPro — Inicio de Sesión");
        scene.setRoot((Parent) buildLogin());
    }

    // ── Panel Admin (tu app original) ────────────────────────────
    private final Map<String, Button> navButtons = new LinkedHashMap<>();
    private String activeSection = "Dashboard";
    private BorderPane adminRoot;

    private Node buildAdminApp() {
        adminRoot = new BorderPane();
        navButtons.clear();
        activeSection = "Dashboard";
        adminRoot.setLeft(buildSidebar());
        adminRoot.setTop(buildTopBar("Dashboard"));
        adminRoot.setCenter(new DashBoardUI().build());
        return adminRoot;
    }

    private VBox buildSidebar() {
        VBox sidebar = new VBox(0);
        sidebar.getStyleClass().add("sidebar");

        VBox logoBlock = new VBox(2);
        logoBlock.setPadding(new Insets(24, 20, 20, 20));
        Label logo    = new Label("🎫  Skyline Tickora");
        logo.getStyleClass().add("sidebar-logo");
        Label logoSub = new Label("Panel Administrador");
        logoSub.getStyleClass().add("sidebar-logo-sub");
        logoBlock.getChildren().addAll(logo, logoSub);

        record NavItem(String icon, String label, String section) {}
        // PONER:
        NavItem[] items = {
                new NavItem("⊞",  "Dashboard",  "Dashboard"),
                new NavItem("📅", "Eventos",    "Eventos"),
                new NavItem("🛒", "Compras",    "Compras"),
                new NavItem("👤", "Usuarios",   "Usuarios"),
                new NavItem("🏟", "Recintos",   "Recintos"),
                new NavItem("⚠",  "Incidencias","Incidencias"),
                new NavItem("📊", "Métricas",   "Métricas"),
        };

        Label navLabel = new Label("NAVEGACIÓN");
        navLabel.getStyleClass().add("nav-section-label");

        VBox nav = new VBox(0);
        nav.getChildren().add(navLabel);
        for (NavItem item : items) {
            Button btn = new Button(item.icon() + "   " + item.label());
            btn.getStyleClass().add("nav-btn");
            btn.setOnAction(e -> switchSection(item.section()));
            navButtons.put(item.section(), btn);
            nav.getChildren().add(btn);
        }
        navButtons.get("Dashboard").getStyleClass().add("nav-btn-active");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button btnLogout = new Button("⎋   Cerrar Sesión");
        btnLogout.getStyleClass().add("btn-danger");
        btnLogout.setMaxWidth(Double.MAX_VALUE);
        btnLogout.setOnAction(e -> {
            ProxyAcceso.getInstancia().logout();
            volverAlLogin();
        });

        VBox bottom = new VBox(10);
        bottom.setPadding(new Insets(12, 16, 20, 16));

        HBox userChip = new HBox(8);
        userChip.setAlignment(Pos.CENTER_LEFT);
        userChip.setPadding(new Insets(6, 12, 6, 12));
        userChip.setStyle("-fx-background-color: -bg-hover; -fx-background-radius: 20;");
        Label av = new Label("A");
        av.setStyle("-fx-background-color: -accent; -fx-background-radius: 14; " +
                "-fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-min-width: 26; -fx-min-height: 26; -fx-alignment: center; -fx-font-size: 11px;");
        Label name = new Label("Administrador");
        name.setStyle("-fx-text-fill: -text-primary; -fx-font-size: 12px;");
        userChip.getChildren().addAll(av, name);

        bottom.getChildren().addAll(userChip, btnLogout);

        sidebar.getChildren().addAll(logoBlock, nav, spacer, bottom);
        return sidebar;
    }

    private HBox buildTopBar(String section) {
        HBox bar = new HBox();
        bar.getStyleClass().add("topbar");
        bar.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label(sectionTitle(section));
        title.getStyleClass().add("topbar-title");
        bar.getChildren().add(title);
        return bar;
    }

    private void switchSection(String section) {
        navButtons.get(activeSection).getStyleClass().remove("nav-btn-active");
        activeSection = section;
        navButtons.get(section).getStyleClass().add("nav-btn-active");
        adminRoot.setTop(buildTopBar(section));
        Node view = switch (section) {
            case "Dashboard" -> new DashBoardUI().build();
            case "Eventos"   -> new EventosUI().build();
            case "Compras"   -> new ComprasUI().build();
            case "Usuarios"  -> new UsuarioUI().build();
            // PONER:
            case "Recintos"    -> new RecintosUI().build();
            case "Incidencias" -> new IncidenciasUI().build();
            case "Métricas"    -> new MetricasUI().build();
            default            -> new DashBoardUI().build();
        };
        adminRoot.setCenter(view);
    }

    private String sectionTitle(String section) {
        return switch (section) {
            case "Dashboard" -> "⊞   Panel Principal";
            case "Eventos"   -> "📅   Gestión de Eventos";
            case "Compras"   -> "🛒   Gestión de Compras";
            case "Usuarios"  -> "👤   Gestión de Usuarios";
            case "Métricas"  -> "📊   Métricas y Análisis";
            case "Recintos"    -> "🏟   Gestión de Recintos";
            case "Incidencias" -> "⚠   Registro de Incidencias";
            default          -> section;
        };
    }

    public static void main(String[] args) { launch(args); }
}