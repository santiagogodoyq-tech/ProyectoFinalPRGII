package co.edu.uniquindio.poo.proyectofinalprgii.UI;

import co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas.*;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.Enums.EstadoCompra;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.Estructural.Proxy.ProxyAcceso;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.Comportamental.Strategy.*;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.ReporteServicio;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.ServicioCorreo;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class UsuarioAppUI {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd MMM yyyy  HH:mm");
    private final DataStore ds = DataStore.get();
    private final UsuarioFinal usuario;
    private final Runnable onLogout;
    private String seccionActiva = "Eventos";

    private BorderPane root;

    public UsuarioAppUI(UsuarioFinal usuario, Runnable onLogout) {
        this.usuario  = usuario;
        this.onLogout = onLogout;
    }

    public Node build() {
        root = new BorderPane();
        root.setLeft(buildSidebar());
        root.setTop(buildTopBar());
        root.setCenter(buildEventosDisponibles());
        return root;
    }

    private VBox buildSidebar() {
        VBox sidebar = new VBox(0);
        sidebar.getStyleClass().add("sidebar");

        VBox logo = new VBox(2);
        logo.setPadding(new Insets(24, 20, 20, 20));
        Label lbl = new Label("🎫  EventosPro");
        lbl.getStyleClass().add("sidebar-logo");
        Label sub = new Label("Portal de usuario");
        sub.getStyleClass().add("sidebar-logo-sub");
        logo.getChildren().addAll(lbl, sub);

        Label navLbl = new Label("MI CUENTA");
        navLbl.getStyleClass().add("nav-section-label");

        Button btnEventos   = navBtn("🎟   Explorar Eventos", "Eventos");
        Button btnHistorial = navBtn("📋   Mis Compras",      "Historial");
        Button btnPerfil    = navBtn("⚙   Mi Perfil",        "Perfil");

        btnEventos.getStyleClass().add("nav-btn-active");

        btnEventos.setOnAction(e -> {
            limpiarActivo(btnEventos, btnHistorial, btnPerfil);
            btnEventos.getStyleClass().add("nav-btn-active");
            seccionActiva = "Eventos";
            root.setCenter(buildEventosDisponibles());
        });
        btnHistorial.setOnAction(e -> {
            limpiarActivo(btnEventos, btnHistorial, btnPerfil);
            btnHistorial.getStyleClass().add("nav-btn-active");
            seccionActiva = "Historial";
            root.setCenter(buildHistorial());
        });
        btnPerfil.setOnAction(e -> {
            limpiarActivo(btnEventos, btnHistorial, btnPerfil);
            btnPerfil.getStyleClass().add("nav-btn-active");
            seccionActiva = "Perfil";
            root.setCenter(buildEditarPerfil());
        });

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button btnLogout = new Button("⎋   Cerrar Sesión");
        btnLogout.getStyleClass().add("btn-danger");
        btnLogout.setMaxWidth(Double.MAX_VALUE);
        btnLogout.setOnAction(e -> {
            ProxyAcceso.getInstancia().logout();
            onLogout.run();
        });

        HBox userChip = new HBox(10);
        userChip.setAlignment(Pos.CENTER_LEFT);
        userChip.setPadding(new Insets(8, 12, 8, 12));
        userChip.setStyle("-fx-background-color: rgba(124,58,237,0.12); -fx-background-radius: 10;");
        Label av = new Label(usuario.getNombre().substring(0, 1).toUpperCase());
        av.setStyle("-fx-background-color: #7c3aed; -fx-background-radius: 18; " +
                "-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px; " +
                "-fx-min-width: 34; -fx-min-height: 34; -fx-alignment: center;");
        VBox info = new VBox(1);
        Label nm   = new Label(usuario.getNombre());
        nm.setStyle("-fx-text-fill: #e6edf3; -fx-font-size: 12px; -fx-font-weight: bold;");
        Label role = new Label("Usuario");
        role.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 10px;");
        info.getChildren().addAll(nm, role);
        userChip.getChildren().addAll(av, info);

        VBox bottom = new VBox(10);
        bottom.setPadding(new Insets(12, 16, 20, 16));
        bottom.getChildren().addAll(userChip, btnLogout);

        VBox navGroup = new VBox(0, navLbl, btnEventos, btnHistorial, btnPerfil);
        sidebar.getChildren().addAll(logo, navGroup, spacer, bottom);
        return sidebar;
    }

    private Button navBtn(String texto, String seccion) {
        Button btn = new Button(texto);
        btn.getStyleClass().add("nav-btn");
        return btn;
    }

    private void limpiarActivo(Button... btns) {
        for (Button b : btns) b.getStyleClass().remove("nav-btn-active");
    }

    private HBox buildTopBar() {
        HBox bar = new HBox();
        bar.getStyleClass().add("topbar");
        bar.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label(seccionActiva.equals("Eventos") ? "🎟   Explorar Eventos" : "📋   Mis Compras");
        title.getStyleClass().add("topbar-title");
        bar.getChildren().add(title);
        return bar;
    }

    // ── Vista: Eventos disponibles ────────────────────────────────
    private Node buildEventosDisponibles() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(28, 32, 32, 32));

        Label title = new Label("Eventos Disponibles");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #e6edf3;");
        Label sub = new Label("Haz clic en \"Comprar\" para adquirir tu entrada");
        sub.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 13px;");

        String fieldStyle = "-fx-background-color: #1c2128; -fx-text-fill: #e6edf3; " +
                "-fx-prompt-text-fill: #8b949e; -fx-border-color: rgba(255,255,255,0.1); " +
                "-fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8 12;";

        TextField fBuscar = new TextField();
        fBuscar.setPromptText("🔍  Buscar evento...");
        fBuscar.setPrefWidth(200);
        fBuscar.setStyle(fieldStyle);

        ComboBox<String> cbCat = new ComboBox<>();
        cbCat.getItems().addAll("Todas", "Música", "Clásica", "Cultural", "Tech", "Deportes");
        cbCat.setValue("Todas");

        ComboBox<String> cbCiudad = new ComboBox<>();
        cbCiudad.getItems().add("Todas las ciudades");
        ds.eventos.stream().map(Evento::getCiudad).distinct().sorted()
                .forEach(cbCiudad.getItems()::add);
        cbCiudad.setValue("Todas las ciudades");

        TextField fPrecioMin = new TextField();
        fPrecioMin.setPromptText("Precio mín"); fPrecioMin.setPrefWidth(100); fPrecioMin.setStyle(fieldStyle);
        TextField fPrecioMax = new TextField();
        fPrecioMax.setPromptText("Precio máx"); fPrecioMax.setPrefWidth(100); fPrecioMax.setStyle(fieldStyle);

        javafx.scene.control.DatePicker dpDesde = new javafx.scene.control.DatePicker();
        dpDesde.setPromptText("Desde"); dpDesde.setPrefWidth(130);
        javafx.scene.control.DatePicker dpHasta = new javafx.scene.control.DatePicker();
        dpHasta.setPromptText("Hasta"); dpHasta.setPrefWidth(130);

        FlowPane filtros = new FlowPane(10, 8);
        filtros.setAlignment(Pos.CENTER_LEFT);
        filtros.getChildren().addAll(fBuscar, cbCat, cbCiudad, fPrecioMin, fPrecioMax, dpDesde, dpHasta);

        FlowPane grid = new FlowPane(16, 16);
        grid.setPrefWrapLength(900);

        Runnable refrescar = () -> {
            grid.getChildren().clear();
            String txt    = fBuscar.getText().toLowerCase().trim();
            String cat    = cbCat.getValue();
            String ciudad = cbCiudad.getValue();
            double precMin = fPrecioMin.getText().isBlank() ? 0 : Double.parseDouble(fPrecioMin.getText().replaceAll("[^\\d.]", ""));
            double precMax = fPrecioMax.getText().isBlank() ? Double.MAX_VALUE : Double.parseDouble(fPrecioMax.getText().replaceAll("[^\\d.]", ""));

            for (Evento ev : ds.eventos) {
                double precioEv = ev.getRecinto().getZonas().stream()
                        .flatMap(z -> z.getAsientos().stream())
                        .mapToDouble(a -> a.getPrecioSegunTipo())
                        .min().orElse(0);

                boolean okTxt    = txt.isEmpty() || ev.getNombre().toLowerCase().contains(txt) || ev.getCiudad().toLowerCase().contains(txt);
                boolean okCat    = "Todas".equals(cat) || cat.equals(ev.getCategoria());
                boolean okCiudad = "Todas las ciudades".equals(ciudad) || ciudad.equals(ev.getCiudad());
                boolean okPrecio = precioEv >= precMin && precioEv <= precMax;
                boolean okDesde  = dpDesde.getValue() == null || !ev.getFecha().toLocalDate().isBefore(dpDesde.getValue());
                boolean okHasta  = dpHasta.getValue() == null || !ev.getFecha().toLocalDate().isAfter(dpHasta.getValue());

                if (okTxt && okCat && okCiudad && okPrecio && okDesde && okHasta)
                    grid.getChildren().add(buildEventoCard(ev));
            }
        };
        refrescar.run();
        fBuscar.textProperty().addListener((o, a, b) -> refrescar.run());
        fPrecioMin.textProperty().addListener((o, a, b) -> { try { refrescar.run(); } catch (Exception ignored) {} });
        fPrecioMax.textProperty().addListener((o, a, b) -> { try { refrescar.run(); } catch (Exception ignored) {} });
        cbCat.setOnAction(e -> refrescar.run());
        cbCiudad.setOnAction(e -> refrescar.run());
        dpDesde.setOnAction(e -> refrescar.run());
        dpHasta.setOnAction(e -> refrescar.run());

        ScrollPane scroll = new ScrollPane(grid);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("scroll-pane");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        root.getChildren().addAll(title, sub, filtros, scroll);
        VBox.setVgrow(root, Priority.ALWAYS);
        return root;
    }

    private VBox buildEventoCard(Evento ev) {
        VBox card = new VBox(10);
        card.setPrefWidth(280);
        card.setStyle(
                "-fx-background-color: #1c2128; -fx-background-radius: 14;" +
                        "-fx-border-color: rgba(255,255,255,0.08); -fx-border-radius: 14;" +
                        "-fx-border-width: 1; -fx-padding: 18;"
        );

        String badgeColor = switch (ev.getCategoria()) {
            case "Música"   -> "#a78bfa";
            case "Clásica"  -> "#3b82f6";
            case "Cultural" -> "#10b981";
            case "Tech"     -> "#f59e0b";
            default         -> "#8b949e";
        };
        Label catBadge = new Label(ev.getCategoria());
        catBadge.setStyle(
                "-fx-background-color: rgba(124,58,237,0.15); -fx-text-fill: " + badgeColor + "; " +
                        "-fx-background-radius: 20; -fx-padding: 3 10; -fx-font-size: 11px; -fx-font-weight: bold;"
        );

        Label nombre = new Label(ev.getNombre());
        nombre.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #e6edf3;");
        nombre.setWrapText(true);

        Label ciudad = new Label("📍 " + ev.getCiudad() + "  ·  " + ev.getRecinto().getNombre());
        ciudad.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 12px;");
        Label fecha = new Label("📅 " + ev.getFecha().format(FMT));
        fecha.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 12px;");
        Label desc = new Label(ev.getDescripcion());
        desc.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 11px;");
        desc.setWrapText(true);

        // ── NUEVO: mostrar política de reembolso en la card ──
        Label politicaLbl = new Label("📋 " + ev.getPoliticas());
        politicaLbl.setStyle("-fx-text-fill: #f59e0b; -fx-font-size: 10px; -fx-wrap-text: true;");
        politicaLbl.setWrapText(true);

        Separator sep = new Separator();

        // ── NUEVO: botón "Ver Reglas" ──
        Button btnReglas = new Button("📋 Ver Reglas");
        btnReglas.setMaxWidth(Double.MAX_VALUE);
        btnReglas.setStyle("-fx-background-color: transparent; -fx-text-fill: #f59e0b; " +
                "-fx-border-color: rgba(245,158,11,0.4); -fx-border-radius: 8; " +
                "-fx-background-radius: 8; -fx-padding: 6 0; -fx-cursor: hand; -fx-font-size: 11px;");
        btnReglas.setOnAction(e -> mostrarReglasEvento(ev));

        Button btnComprar = new Button("🛒  Comprar Entrada");
        btnComprar.setMaxWidth(Double.MAX_VALUE);
        btnComprar.setStyle(
                "-fx-background-color: #7c3aed; -fx-text-fill: white; " +
                        "-fx-font-weight: bold; -fx-background-radius: 8; " +
                        "-fx-padding: 10 0; -fx-cursor: hand; -fx-font-size: 13px;"
        );
        btnComprar.setOnMouseEntered(e -> btnComprar.setStyle(btnComprar.getStyle().replace("#7c3aed", "#6d28d9")));
        btnComprar.setOnMouseExited(e  -> btnComprar.setStyle(btnComprar.getStyle().replace("#6d28d9", "#7c3aed")));

        btnComprar.setOnAction(e ->
                new SelectorAsientosUI(ev, asientoSeleccionado -> {
                    double _base = asientoSeleccionado.getPrecioSegunTipo();
                    double _totalConImpuestos = _base + (_base * 0.18) + (_base * 0.06);
                    new PagoDialogUI(
                            _totalConImpuestos,
                                (estrategia, metodoPago, pagar, conMerch, conSeguro, conParqueadero) -> {
                                    Compra c = ds.nuevaCompra(usuario, ev, asientoSeleccionado, estrategia, metodoPago);

                                    // Guardar servicios en la Compra (persisten aunque se pague después)
                                    c.guardarServicios(conMerch, conSeguro, conParqueadero);
                                    if (conMerch || conSeguro || conParqueadero) {
                                        ds.log("🎁 Servicios guardados para compra " + c.getIdCompra()
                                                + " — Total: $" + String.format("%,.0f", c.getTotal()));
                                    }

                                    if (pagar) {
                                        c.setEstrategiaPago(estrategia);
                                        c.pagar(null);
                                        ServicioCorreo.notificarConfirmacionCompra(c);
                                    }
                                    mostrarConfirmacion(c, ev);
                                    this.root.setCenter(buildEventosDisponibles());
                                }
                    ).mostrar();
                }).mostrar()
        );

        card.getChildren().addAll(catBadge, nombre, ciudad, fecha, desc, politicaLbl, sep, btnReglas, btnComprar);
        return card;
    }

    // ── NUEVO: diálogo de reglas del evento ──────────────────────
    private void mostrarReglasEvento(Evento ev) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Reglas del Evento");
        a.setHeaderText(null);
        a.getDialogPane().setStyle("-fx-background-color: #1c2128; -fx-min-width: 480;");

        VBox content = new VBox(12);
        content.setPadding(new Insets(20));

        Label titulo = new Label("📋  " + ev.getNombre());
        titulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #a78bfa;");

        Label politicaTitle = new Label("Política de reembolso:");
        politicaTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #e6edf3; -fx-font-size: 13px;");
        Label politicaText = new Label(ev.getPoliticas());
        politicaText.setStyle("-fx-text-fill: #f59e0b; -fx-font-size: 12px;");
        politicaText.setWrapText(true);

        Label reembolsoTitle = new Label("¿Cómo funciona el reembolso?");
        reembolsoTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #e6edf3; -fx-font-size: 13px;");

        Label reembolsoInfo = new Label(
                "• Más de 7 días antes del evento → reembolso del 100%\n" +
                        "• Entre 2 y 6 días antes → reembolso del 75%\n" +
                        "• Menos de 2 días antes → reembolso del 20%\n\n" +
                        "Para solicitar un reembolso, ve a 'Mis Compras' y haz clic\n" +
                        "en '↩ Reembolsar' sobre la compra correspondiente.\n\n" +
                        "El reembolso se aplicará al método de pago original en un\n" +
                        "plazo de 5-10 días hábiles."
        );
        reembolsoInfo.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 12px;");
        reembolsoInfo.setWrapText(true);

        Label reglasTit = new Label("Reglas generales:");
        reglasTit.setStyle("-fx-font-weight: bold; -fx-text-fill: #e6edf3; -fx-font-size: 13px;");
        Label reglasText = new Label(
                "• Las entradas son personales e intransferibles.\n" +
                        "• Debes presentar tu QR al ingreso al recinto.\n" +
                        "• El organizador puede modificar o cancelar el evento.\n" +
                        "• En caso de cancelación por el organizador, el reembolso es del 100%."
        );
        reglasText.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 12px;");
        reglasText.setWrapText(true);

        content.getChildren().addAll(
                titulo, new Separator(),
                politicaTitle, politicaText, new Separator(),
                reembolsoTitle, reembolsoInfo, new Separator(),
                reglasTit, reglasText
        );
        a.getDialogPane().setContent(content);
        a.showAndWait();
    }

    // ── Vista: Historial del usuario ──────────────────────────────
    private Node buildHistorial() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(28, 32, 32, 32));

        Label title = new Label("Mis Compras");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #e6edf3;");

        // ── NUEVO: panel informativo de reembolsos ──
        VBox infoReembolso = new VBox(6);
        infoReembolso.setPadding(new Insets(12, 16, 12, 16));
        infoReembolso.setStyle("-fx-background-color: rgba(245,158,11,0.1); -fx-background-radius: 10; " +
                "-fx-border-color: rgba(245,158,11,0.3); -fx-border-radius: 10;");
        Label infoTit = new Label("💡  ¿Cómo funcionan los reembolsos?");
        infoTit.setStyle("-fx-font-weight: bold; -fx-text-fill: #f59e0b; -fx-font-size: 12px;");
        Label infoTxt = new Label(
                "+7 días → 100%  ·  2-6 días → 75%  ·  <2 días → 20%  " +
                        "·  Solo para compras PAGADAS o CONFIRMADAS.");
        infoTxt.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 11px;");
        infoTxt.setWrapText(true);
        infoReembolso.getChildren().addAll(infoTit, infoTxt);

        var misCompras = ds.compras.filtered(
                c -> c.getUsuario().getIdUsuario().equals(usuario.getIdUsuario())
        );

        if (misCompras.isEmpty()) {
            Label empty = new Label("No tienes compras aún. ¡Explora los eventos disponibles!");
            empty.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 14px;");
            root.getChildren().addAll(title, infoReembolso, empty);
            return root;
        }

        VBox lista = new VBox(12);
        for (Compra c : misCompras) {
            lista.getChildren().add(buildCompraRow(c));
        }

        HBox descarga = new HBox(10);
        descarga.setAlignment(Pos.CENTER_LEFT);
        Button btnCSV = new Button("⬇ Descargar CSV");
        Button btnPDF = new Button("⬇ Descargar PDF");
        btnCSV.setStyle("-fx-background-color: rgba(16,185,129,0.15); -fx-text-fill: #10b981; " +
                "-fx-border-color: rgba(16,185,129,0.35); -fx-border-radius: 8; " +
                "-fx-background-radius: 8; -fx-padding: 8 16; -fx-cursor: hand;");
        btnPDF.setStyle("-fx-background-color: rgba(59,130,246,0.15); -fx-text-fill: #3b82f6; " +
                "-fx-border-color: rgba(59,130,246,0.35); -fx-border-radius: 8; " +
                "-fx-background-radius: 8; -fx-padding: 8 16; -fx-cursor: hand;");

        ArrayList<Compra> misComprasList = new ArrayList<>(misCompras);
        btnCSV.setOnAction(e -> ReporteServicio.exportarCSV(misComprasList, btnCSV.getScene().getWindow()));
        btnPDF.setOnAction(e -> ReporteServicio.exportarPDF(misComprasList, btnPDF.getScene().getWindow()));
        descarga.getChildren().addAll(btnCSV, btnPDF);

        ScrollPane scroll = new ScrollPane(lista);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("scroll-pane");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        root.getChildren().addAll(title, infoReembolso, descarga, scroll);
        VBox.setVgrow(root, Priority.ALWAYS);
        return root;
    }

    private HBox buildCompraRow(Compra c) {
        HBox row = new HBox(16);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(14, 18, 14, 18));
        row.setStyle(
                "-fx-background-color: #1c2128; -fx-background-radius: 12;" +
                        "-fx-border-color: rgba(255,255,255,0.08); -fx-border-radius: 12;" +
                        "-fx-border-width: 1;"
        );

        // Botón pagar compras pendientes
        if (c.getEstado() == EstadoCompra.CREADA) {
            Button btnPagar = new Button("💳 Pagar");
            btnPagar.setStyle(
                    "-fx-background-color: rgba(16,185,129,0.15); -fx-text-fill: #10b981; " +
                            "-fx-background-radius: 8; -fx-padding: 5 12; -fx-cursor: hand; " +
                            "-fx-border-color: rgba(16,185,129,0.35); -fx-border-radius: 8; -fx-font-size: 11px;");
            btnPagar.setOnAction(ev2 -> {
                // ── BUGFIX precio: usa el total real de la compra, no 305.000 fijo ──
                new PagoDialogUI(c.getTotal(),
                        (estrategia, metodoPago, pagar, conMerch, conSeguro, conParqueadero) -> {
                            if (pagar) {
                                c.setEstrategiaPago(estrategia);
                                c.pagar(null);
                                ServicioCorreo.notificarConfirmacionCompra(c);
                                ds.log("💳 Pago completado: " + c.getIdCompra());
                                root.setCenter(buildHistorial());
                            }
                        }).mostrar();
            });
            row.getChildren().add(btnPagar);
        }

        // Botón reembolso
        if (c.getEstado() == EstadoCompra.PAGADA || c.getEstado() == EstadoCompra.CONFIRMADA) {
            Button btnReembolso = new Button("↩ Reembolsar");
            btnReembolso.setStyle(
                    "-fx-background-color: rgba(239,68,68,0.15); -fx-text-fill: #ef4444; " +
                            "-fx-background-radius: 8; -fx-padding: 5 12; -fx-cursor: hand; " +
                            "-fx-border-color: rgba(239,68,68,0.35); -fx-border-radius: 8; -fx-font-size: 11px;");
            btnReembolso.setOnAction(ev2 -> mostrarDialogoReembolso(c));
            row.getChildren().add(btnReembolso);
        }

        // ── NUEVO: botón Ver Reglas en historial ──
        Button btnReglas = new Button("📋 Reglas");
        btnReglas.setStyle(
                "-fx-background-color: rgba(245,158,11,0.12); -fx-text-fill: #f59e0b; " +
                        "-fx-background-radius: 8; -fx-padding: 5 10; -fx-cursor: hand; " +
                        "-fx-border-color: rgba(245,158,11,0.3); -fx-border-radius: 8; -fx-font-size: 10px;");
        btnReglas.setOnAction(e -> mostrarReglasEvento(c.getEvento()));
        row.getChildren().add(btnReglas);

        Label ico = new Label("🎫");
        ico.setStyle("-fx-font-size: 22px;");

        VBox info = new VBox(4);
        Label ev = new Label(c.getEvento().getNombre());
        ev.setStyle("-fx-font-weight: bold; -fx-text-fill: #e6edf3; -fx-font-size: 14px;");
        Label det = new Label(c.getIdCompra() + "  ·  " + c.getFecha().format(FMT));
        det.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 11px;");
        info.getChildren().addAll(ev, det);
        HBox.setHgrow(info, Priority.ALWAYS);

        VBox right = new VBox(6);
        right.setAlignment(Pos.CENTER_RIGHT);
        Label monto = new Label(String.format("$%,.0f", c.getTotal()));
        monto.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #10b981;");
        String badgeCss = switch (c.getEstado()) {
            case PAGADA, CONFIRMADA -> "badge-green";
            case CREADA             -> "badge-amber";
            default                 -> "badge-red";
        };
        Label badge = new Label(c.getEstado().name());
        badge.getStyleClass().add(badgeCss);
        right.getChildren().addAll(monto, badge);

        row.getChildren().addAll(ico, info, right);
        return row;
    }

    private void mostrarConfirmacion(Compra c, Evento ev) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("¡Compra realizada!");
        a.setHeaderText(null);
        a.getDialogPane().setStyle("-fx-background-color: #1c2128;");

        VBox content = new VBox(12);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(16));

        Label ico   = new Label("✅"); ico.setStyle("-fx-font-size: 36px;");
        Label msg   = new Label("¡Tu entrada fue reservada!");
        msg.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #10b981;");
        Label det   = new Label("Evento: " + ev.getNombre());
        det.setStyle("-fx-text-fill: #8b949e;");
        // ── BUGFIX precio en confirmación: muestra el total real ──
        Label total = new Label("Total: " + String.format("$%,.0f", c.getTotal()));
        total.setStyle("-fx-font-weight: bold; -fx-text-fill: #e6edf3; -fx-font-size: 14px;");
        Label id    = new Label("ID: " + c.getIdCompra());
        id.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 11px; -fx-font-family: monospace;");

        // ── NUEVO: mostrar política de reembolso en confirmación ──
        Label politicaLbl = new Label("📋 " + ev.getPoliticas());
        politicaLbl.setStyle("-fx-text-fill: #f59e0b; -fx-font-size: 11px;");
        politicaLbl.setWrapText(true);

        content.getChildren().addAll(ico, msg, det, total, id, new Separator(), politicaLbl);
        a.getDialogPane().setContent(content);
        a.showAndWait();
    }

    private void mostrarDialogoReembolso(Compra compra) {
        long diasHastaEvento = ChronoUnit.DAYS.between(LocalDateTime.now(), compra.getEvento().getFecha());

        boolean esAnticipada = diasHastaEvento >= 2;
        double porcentaje    = esAnticipada ? (diasHastaEvento >= 7 ? 100 : 75) : 20;
        double montoReembolso = compra.getTotal() * porcentaje / 100;
        String tipoStr = esAnticipada ? "anticipada" : "tardía";

        Alert conf = new Alert(Alert.AlertType.CONFIRMATION);
        conf.setTitle("Solicitar Reembolso");
        conf.setHeaderText(null);
        conf.getDialogPane().setStyle("-fx-background-color: #1c2128;");

        VBox c = new VBox(10);
        c.setPadding(new Insets(16));
        Label t = new Label("↩  Solicitar reembolso");
        t.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #a78bfa;");
        Label info = new Label(
                "Compra: " + compra.getIdCompra() +
                        "\nEvento: " + compra.getEvento().getNombre() +
                        "\nDías hasta el evento: " + diasHastaEvento +
                        "\nTipo de cancelación: " + tipoStr +
                        "\nReembolso (" + (int) porcentaje + "%): $" + String.format("%,.0f", montoReembolso)
        );
        info.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 13px;");

        // ── NUEVO: info sobre cómo funciona el reembolso ──
        Label politicaLbl = new Label("Política del evento: " + compra.getEvento().getPoliticas());
        politicaLbl.setStyle("-fx-text-fill: #f59e0b; -fx-font-size: 11px;");
        politicaLbl.setWrapText(true);

        c.getChildren().addAll(t, info, new Separator(), politicaLbl);
        conf.getDialogPane().setContent(c);

        conf.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.OK) {
                CancelacionStrategy estrategia = esAnticipada
                        ? new CancelacionAnticipadaStrategy()
                        : new CancelacionTardiaStrategy();
                compra.setEstrategiaCancelacion(estrategia);
                compra.reembolsar(); // liberará asientos automáticamente (fix en Compra.java)
                ServicioCorreo.notificarReembolso(compra, montoReembolso);
                ds.log("↩ Reembolso procesado: " + compra.getIdCompra()
                        + " — $" + String.format("%,.0f", montoReembolso));
                ds.compras.remove(compra);
                root.setCenter(buildHistorial());
            }
        });
    }

    private Node buildEditarPerfil() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(28, 32, 32, 32));
        root.setMaxWidth(500);

        Label title = new Label("⚙  Mi Perfil");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #e6edf3;");

        VBox form = new VBox(14);
        form.setStyle("-fx-background-color: #1c2128; -fx-background-radius: 12; " +
                "-fx-border-color: rgba(255,255,255,0.08); -fx-border-radius: 12; -fx-padding: 22;");

        TextField fNombre = fieldPerfil(usuario.getNombre());
        TextField fCorreo = fieldPerfil(usuario.getCorreo());
        TextField fTel    = fieldPerfil(usuario.getTelefono());
        PasswordField fPassActual = new PasswordField();
        fPassActual.setPromptText("Contraseña actual"); fPassActual.setStyle(fieldStylePerfil());
        PasswordField fPassNueva = new PasswordField();
        fPassNueva.setPromptText("Nueva contraseña (dejar vacío para no cambiar)"); fPassNueva.setStyle(fieldStylePerfil());

        Label errorLbl = new Label(""); errorLbl.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 12px;"); errorLbl.setVisible(false);

        Button btnGuardar = new Button("💾  Guardar Cambios");
        btnGuardar.setStyle("-fx-background-color: #7c3aed; -fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-background-radius: 8; -fx-padding: 10 20; -fx-cursor: hand;");
        btnGuardar.setOnAction(e -> {
            if (!fPassActual.getText().equals(usuario.getContrasena())) {
                errorLbl.setText("Contraseña actual incorrecta."); errorLbl.setVisible(true); return;
            }
            usuario.modificarPerfil(fNombre.getText().trim(), fCorreo.getText().trim(), fTel.getText().trim());
            if (!fPassNueva.getText().isBlank()) usuario.setContrasena(fPassNueva.getText());
            errorLbl.setVisible(false);
            DataStore.get().log("✏ Perfil actualizado: " + usuario.getNombre());
            Alert ok = new Alert(Alert.AlertType.INFORMATION, "Perfil actualizado correctamente.");
            ok.setHeaderText(null); ok.showAndWait();
        });

        form.getChildren().addAll(
                lbl2("Nombre"), fNombre, lbl2("Correo"), fCorreo, lbl2("Teléfono"), fTel,
                new Separator(),
                lbl2("Contraseña actual (requerida)"), fPassActual,
                lbl2("Nueva contraseña"), fPassNueva,
                errorLbl, btnGuardar
        );
        root.getChildren().addAll(title, form);
        return root;
    }

    private TextField fieldPerfil(String valor) {
        TextField tf = new TextField(valor); tf.setStyle(fieldStylePerfil()); return tf;
    }
    private String fieldStylePerfil() {
        return "-fx-background-color: #0d1117; -fx-text-fill: #e6edf3; " +
                "-fx-border-color: rgba(255,255,255,0.1); -fx-border-radius: 8; " +
                "-fx-background-radius: 8; -fx-padding: 9 12;";
    }
    private Label lbl2(String t) {
        Label l = new Label(t); l.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 11px;"); return l;
    }
}