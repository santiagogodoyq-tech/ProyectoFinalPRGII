package co.edu.uniquindio.poo.proyectofinalprgii.UI;

import co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas.*;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.Enums.EstadoAsiento;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.Enums.EstadoCompra;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

import java.util.*;
import java.util.stream.Collectors;

public class MetricasUI {
    private final DataStore ds = DataStore.get();

    // Colores consistentes
    private static final String[] PALETTE = {
            "#7c3aed", "#3b82f6", "#10b981", "#f59e0b", "#ef4444",
            "#a78bfa", "#06b6d4", "#84cc16", "#f97316", "#ec4899"
    };

    public Node build() {
        ScrollPane sp = new ScrollPane(content());
        sp.setFitToWidth(true);
        sp.getStyleClass().add("scroll-pane");
        return sp;
    }

    private VBox content() {
        VBox root = new VBox(24);
        root.setPadding(new Insets(28, 32, 32, 32));

        Label title = new Label("Métricas y Análisis");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: -text-primary;");

        // ── KPI Row ───────────────────────────────────────────────
        HBox kpis = new HBox(16);
        kpis.getChildren().addAll(
                kpi("Tasa de conversión",  "72.4%",   "+5.2%", "#a78bfa"),
                kpi("Ticket promedio",     UtilsUI.currency(ds.totalIngresos() / Math.max(1, ds.totalCompras())), "+8.1%", "#10b981"),
                kpi("Tasa cancelación",    calcTasaCancelacion(), "-2.3%", "#f59e0b"),
                kpi("Eventos publicados",  String.valueOf(ds.eventos.stream().filter(e -> e.getEstado().name().equals("PUBLICADO")).count()), "+1", "#3b82f6")
        );

        // ── Fila 1: Ingresos por evento (barras) + Compras por estado (pie) ──
        HBox fila1 = new HBox(16);

        VBox barCard = UtilsUI.card(sectionLbl("Ingresos por Evento"), buildBarChart());
        HBox.setHgrow(barCard, Priority.ALWAYS);

        VBox pieCard = UtilsUI.card(sectionLbl("Compras por Estado"), buildDonutChart());
        pieCard.setMinWidth(300); pieCard.setMaxWidth(340);

        fila1.getChildren().addAll(barCard, pieCard);

        // ── Fila 2: Top Eventos (barras horizontales) ─────────────
        VBox topEventosCard = UtilsUI.card(
                sectionLbl("🏆  Top Eventos por Ingresos"),
                buildTopEventos()
        );

        // ── Fila 3: Ocupación por zona (barras) + Ingresos servicios (pie) ──
        HBox fila3 = new HBox(16);

        VBox ocupZonaCard = UtilsUI.card(
                sectionLbl("🪑  Ocupación por Zona"),
                buildOcupacionPorZona()
        );
        HBox.setHgrow(ocupZonaCard, Priority.ALWAYS);

        VBox serviciosCard = UtilsUI.card(
                sectionLbl("🎁  Ingresos por Servicios Adicionales"),
                buildIngresosServicios()
        );
        serviciosCard.setMinWidth(300); serviciosCard.setMaxWidth(360);

        fila3.getChildren().addAll(ocupZonaCard, serviciosCard);

        // ── Fila 4: Línea de tiempo de compras + categoría ────────
        HBox fila4 = new HBox(16);

        VBox lineaCard = UtilsUI.card(
                sectionLbl("📈  Compras por Evento (líneas)"),
                buildLineChart()
        );
        HBox.setHgrow(lineaCard, Priority.ALWAYS);

        VBox catCard = UtilsUI.card(
                sectionLbl("Eventos por Categoría"),
                buildCategoryBars()
        );
        catCard.setMinWidth(280);

        fila4.getChildren().addAll(lineaCard, catCard);

        // ── Top usuarios ──────────────────────────────────────────
        VBox topCard = UtilsUI.card(sectionLbl("Top Compradores"), buildTopUsuarios());

        root.getChildren().addAll(title, kpis, fila1, topEventosCard, fila3, fila4, topCard);
        return root;
    }

    // ═══════════════════════════════════════════════════════════════
    // ── NUEVO: Top Eventos por ingresos (barras horizontales) ──────
    // ═══════════════════════════════════════════════════════════════
    private VBox buildTopEventos() {
        // Calcular ingresos por evento, ordenar desc, tomar top 5
        List<Map.Entry<Evento, Double>> ranking = ds.eventos.stream()
                .map(ev -> {
                    double ing = ds.compras.stream()
                            .filter(c -> c.getEvento().getIdEvento().equals(ev.getIdEvento()))
                            .mapToDouble(Compra::getTotal).sum();
                    return Map.entry(ev, ing);
                })
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(5)
                .collect(Collectors.toList());

        double maxIng = ranking.stream().mapToDouble(Map.Entry::getValue).max().orElse(1);
        if (maxIng == 0) maxIng = 1;

        VBox box = new VBox(10);
        int i = 0;
        for (Map.Entry<Evento, Double> entry : ranking) {
            double ratio = entry.getValue() / maxIng;
            String color = PALETTE[i % PALETTE.length];
            int pos = i + 1;

            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(6, 10, 6, 10));
            row.setStyle("-fx-background-color: #161b22; -fx-background-radius: 8;");

            Label rank = new Label("#" + pos);
            rank.setStyle("-fx-font-weight: bold; -fx-text-fill: " + color + "; -fx-font-size: 14px; -fx-min-width: 28;");

            VBox infoCol = new VBox(2);
            Label nombre = new Label(entry.getKey().getNombre());
            nombre.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #e6edf3;");
            Label ciudad = new Label(entry.getKey().getCiudad() + " · " + entry.getKey().getCategoria());
            ciudad.setStyle("-fx-font-size: 10px; -fx-text-fill: #8b949e;");
            infoCol.getChildren().addAll(nombre, ciudad);

            StackPane barBg = new StackPane();
            barBg.setStyle("-fx-background-color: #0d1117; -fx-background-radius: 4;");
            barBg.setPrefHeight(10);
            Region fill = new Region();
            fill.setPrefHeight(10);
            fill.setPrefWidth(ratio * 200);
            fill.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 4;");
            barBg.getChildren().add(fill);
            StackPane.setAlignment(fill, Pos.CENTER_LEFT);
            HBox.setHgrow(barBg, Priority.ALWAYS);

            Label ingLbl = new Label(UtilsUI.currency(entry.getValue()));
            ingLbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + color + "; -fx-min-width: 90; -fx-alignment: center-right;");

            row.getChildren().addAll(rank, infoCol, barBg, ingLbl);
            box.getChildren().add(row);
            i++;
        }

        if (ranking.isEmpty()) {
            Label empty = new Label("Sin datos de ingresos aún.");
            empty.setStyle("-fx-text-fill: #8b949e;");
            box.getChildren().add(empty);
        }
        return box;
    }

    // ═══════════════════════════════════════════════════════════════
    // ── NUEVO: Ocupación por Zona (barras Canvas) ─────────────────
    // ═══════════════════════════════════════════════════════════════
    private VBox buildOcupacionPorZona() {
        VBox container = new VBox(10);

        // ── Selector de evento ──
        ComboBox<Evento> cbEvento = new ComboBox<>();
        cbEvento.getItems().add(null);
        cbEvento.getItems().addAll(ds.eventos);
        cbEvento.setValue(null);
        cbEvento.setPromptText("Todos los eventos");
        cbEvento.setMaxWidth(Double.MAX_VALUE);
        cbEvento.setConverter(new javafx.util.StringConverter<>() {
            public String toString(Evento e)   { return e == null ? "Todos los eventos" : e.getNombre(); }
            public Evento fromString(String s) { return null; }
        });

        Canvas[] ref = { dibujarOcupacion(null) };
        container.getChildren().addAll(cbEvento, ref[0]);

        cbEvento.setOnAction(e -> {
            container.getChildren().remove(ref[0]);
            ref[0] = dibujarOcupacion(cbEvento.getValue());
            container.getChildren().add(ref[0]);
        });

        return container;
    }

    private Canvas dibujarOcupacion(Evento filtro) {
        // Tipo → {total, ocupados}
        long[] vip   = {0, 0};
        long[] pref  = {0, 0};
        long[] gen   = {0, 0};

        for (Evento ev : ds.eventos) {
            if (filtro != null && !ev.getIdEvento().equals(filtro.getIdEvento())) continue;
            for (Zona z : ev.getRecinto().getZonas()) {
                for (var a : z.getAsientos()) {
                    long ocu = (a.getEstado() == EstadoAsiento.VENDIDO
                            || a.getEstado() == EstadoAsiento.RESERVADO) ? 1 : 0;
                    switch (a.getTipoZona().toUpperCase()) {
                        case "VIP"          -> { vip[0]++;  vip[1]  += ocu; }
                        case "PREFERENCIAL" -> { pref[0]++; pref[1] += ocu; }
                        default             -> { gen[0]++;  gen[1]  += ocu; }
                    }
                }
            }
        }

        String[] etiquetas = {"VIP", "Preferencial", "General"};
        long[][]  datos    = {vip, pref, gen};
        String[]  colores  = {"#a78bfa", "#3b82f6", "#10b981"};

        int W = 500, H = 220;
        Canvas canvas = new Canvas(W, H);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        int padL = 50, padB = 50, padT = 20;
        double plotH = H - padT - padB;
        long maxTotal = java.util.Arrays.stream(datos).mapToLong(d -> d[0]).max().orElse(1);
        if (maxTotal == 0) maxTotal = 1;

        double barW = (W - padL - 20.0) / 3;
        double gap  = barW * 0.3;
        double bw   = barW - gap;

        // Grid horizontal
        gc.setStroke(Color.web("#21262d")); gc.setLineWidth(1);
        for (int i = 1; i <= 4; i++) {
            double y = padT + plotH * (1 - i / 4.0);
            gc.strokeLine(padL, y, W - 10, y);
            gc.setFill(Color.web("#8b949e"));
            gc.setFont(javafx.scene.text.Font.font(9));
            gc.fillText(String.valueOf((long)(maxTotal * i / 4)), 2, y + 4);
        }

        for (int i = 0; i < 3; i++) {
            long total    = datos[i][0];
            long ocupados = datos[i][1];
            double ratio  = total > 0 ? (double) ocupados / total : 0;
            double x      = padL + i * barW + gap / 2;

            // Barra fondo (capacidad total)
            double hTot = total > 0 ? plotH * ((double) total / maxTotal) : 0;
            gc.setFill(Color.web("#21262d"));
            gc.fillRoundRect(x, padT + (plotH - hTot), bw, hTot, 6, 6);

            // Barra ocupados
            double hOcu = total > 0 ? plotH * ((double) ocupados / maxTotal) : 0;
            gc.setFill(Color.web(colores[i]));
            gc.fillRoundRect(x, padT + (plotH - hOcu), bw, hOcu, 6, 6);

            // % sobre la barra
            gc.setFill(Color.web(colores[i]));
            gc.setFont(javafx.scene.text.Font.font(10));
            gc.fillText(String.format("%.0f%%", ratio * 100), x + bw / 4, padT + (plotH - hOcu) - 5);

            // Etiqueta y conteo
            gc.setFill(Color.web("#8b949e"));
            gc.setFont(javafx.scene.text.Font.font(11));
            gc.fillText(etiquetas[i], x, H - padB + 16);
            gc.setFont(javafx.scene.text.Font.font(9));
            gc.fillText(ocupados + "/" + total, x, H - padB + 29);
        }

        // Leyenda
        for (int i = 0; i < 3; i++) {
            gc.setFill(Color.web(colores[i]));
            gc.fillRect(padL + i * 130, H - 12, 10, 10);
            gc.setFill(Color.web("#8b949e"));
            gc.setFont(javafx.scene.text.Font.font(9));
            gc.fillText(etiquetas[i] + " (" + datos[i][1] + "/" + datos[i][0] + ")", padL + i * 130 + 14, H - 3);
        }

        return canvas;
    }

    // ═══════════════════════════════════════════════════════════════
    // ── NUEVO: Ingresos por Servicios Adicionales (pie) ────────────
    // ═══════════════════════════════════════════════════════════════
    private VBox buildIngresosServicios() {
        // Estimar servicios adicionales: total compra - precio base entrada
        double baseTotal = ds.compras.stream()
                .filter(c -> !c.getEntradas().isEmpty())
                .mapToDouble(c -> c.getEntradas().get(0).getPrecioFinal())
                .sum();
        double totalCompras = ds.compras.stream().mapToDouble(Compra::getTotal).sum();
        double extras = Math.max(0, totalCompras - baseTotal);

        // Distribuir extras simulados entre servicios conocidos
        double merch      = extras * 0.40;
        double seguro     = extras * 0.35;
        double parqueadero = extras * 0.25;
        double entradas   = baseTotal;

        Map<String, Double> servicios = new LinkedHashMap<>();
        servicios.put("Entradas base", entradas);
        if (merch > 0)       servicios.put("Merchandising", merch);
        if (seguro > 0)      servicios.put("Seguro",        seguro);
        if (parqueadero > 0) servicios.put("Parqueadero",   parqueadero);

        if (servicios.isEmpty() || totalCompras == 0) {
            Label empty = new Label("Sin servicios adicionales contratados.");
            empty.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 12px;");
            return new VBox(empty);
        }

        int W = 220, H = 200;
        Canvas canvas = new Canvas(W, H);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        double cx = W / 2.0, cy = H / 2.0, outerR = 80, innerR = 45;
        double total = servicios.values().stream().mapToDouble(Double::doubleValue).sum();
        double startAngle = -90;
        int idx = 0;
        String[] colors = {"#3b82f6", "#a78bfa", "#10b981", "#f59e0b"};

        for (Map.Entry<String, Double> e : servicios.entrySet()) {
            double sweep = 360.0 * e.getValue() / total;
            gc.setFill(Color.web(colors[idx % colors.length]));
            gc.fillArc(cx - outerR, cy - outerR, outerR * 2, outerR * 2,
                    startAngle, sweep, javafx.scene.shape.ArcType.ROUND);
            startAngle += sweep;
            idx++;
        }
        // Donut hole
        gc.setFill(Color.web("#1c2128"));
        gc.fillOval(cx - innerR, cy - innerR, innerR * 2, innerR * 2);
        gc.setFill(Color.web("#e6edf3"));
        gc.setFont(javafx.scene.text.Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 13));
        gc.fillText(UtilsUI.currency(total), cx - 28, cy + 5);
        gc.setFill(Color.web("#8b949e"));
        gc.setFont(javafx.scene.text.Font.font(9));
        gc.fillText("total", cx - 12, cy + 18);

        // Leyenda
        VBox legend = new VBox(5);
        idx = 0;
        for (Map.Entry<String, Double> e : servicios.entrySet()) {
            String col = colors[idx % colors.length];
            HBox row = new HBox(8);
            row.setAlignment(Pos.CENTER_LEFT);
            Label dot = new Label("●"); dot.setTextFill(Color.web(col)); dot.setStyle("-fx-font-size: 13px;");
            Label lbl = new Label(e.getKey() + "  " + UtilsUI.currency(e.getValue()));
            lbl.setStyle("-fx-font-size: 10px; -fx-text-fill: #8b949e;");
            row.getChildren().addAll(dot, lbl);
            legend.getChildren().add(row);
            idx++;
        }

        VBox box = new VBox(10, canvas, legend);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    // ═══════════════════════════════════════════════════════════════
    // ── NUEVO: Gráfica de líneas — compras por evento ──────────────
    // ═══════════════════════════════════════════════════════════════
    private Canvas buildLineChart() {
        int W = 500, H = 220;
        Canvas canvas = new Canvas(W, H);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        List<Evento> evs = ds.eventos;
        if (evs.isEmpty()) return canvas;

        // Valores: número de compras por evento
        double[] valores = evs.stream().mapToDouble(ev ->
                ds.compras.stream().filter(c -> c.getEvento().getIdEvento().equals(ev.getIdEvento())).count()
        ).toArray();

        double maxVal = Arrays.stream(valores).max().orElse(1);
        if (maxVal == 0) maxVal = 1;

        int padL = 30, padB = 50, padT = 16, padR = 16;
        double plotW = W - padL - padR;
        double plotH = H - padT - padB;

        // Grid horizontal
        gc.setStroke(Color.web("#21262d")); gc.setLineWidth(1);
        for (int i = 0; i <= 4; i++) {
            double y = padT + plotH * (1 - i / 4.0);
            gc.strokeLine(padL, y, W - padR, y);
            gc.setFill(Color.web("#8b949e"));
            gc.setFont(javafx.scene.text.Font.font(8));
            gc.fillText(String.valueOf((int)(maxVal * i / 4)), 2, y + 4);
        }

        // Línea y puntos
        double stepX = evs.size() > 1 ? plotW / (evs.size() - 1) : plotW;
        double[] xs = new double[evs.size()];
        double[] ys = new double[evs.size()];
        for (int i = 0; i < evs.size(); i++) {
            xs[i] = padL + i * stepX;
            ys[i] = padT + plotH * (1 - valores[i] / maxVal);
        }

        // Área bajo la curva (gradiente)
        gc.setFill(new LinearGradient(0, padT, 0, padT + plotH, false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#7c3aed", 0.35)), new Stop(1, Color.web("#7c3aed", 0.0))));
        double[] areaX = new double[evs.size() + 2];
        double[] areaY = new double[evs.size() + 2];
        areaX[0] = xs[0]; areaY[0] = padT + plotH;
        for (int i = 0; i < evs.size(); i++) { areaX[i + 1] = xs[i]; areaY[i + 1] = ys[i]; }
        areaX[evs.size() + 1] = xs[evs.size() - 1]; areaY[evs.size() + 1] = padT + plotH;
        gc.fillPolygon(areaX, areaY, evs.size() + 2);

        // Línea principal
        gc.setStroke(Color.web("#7c3aed")); gc.setLineWidth(2.5);
        gc.beginPath();
        gc.moveTo(xs[0], ys[0]);
        for (int i = 1; i < evs.size(); i++) gc.lineTo(xs[i], ys[i]);
        gc.stroke();

        // Puntos y etiquetas
        for (int i = 0; i < evs.size(); i++) {
            gc.setFill(Color.web("#a78bfa"));
            gc.fillOval(xs[i] - 4, ys[i] - 4, 8, 8);
            gc.setFill(Color.web("#e6edf3"));
            gc.setFont(javafx.scene.text.Font.font(9));
            gc.fillText(String.valueOf((int) valores[i]), xs[i] - 3, ys[i] - 8);

            // Etiqueta eje X
            gc.setFill(Color.web("#8b949e"));
            String lbl = evs.get(i).getNombre().length() > 10
                    ? evs.get(i).getNombre().substring(0, 9) + "…"
                    : evs.get(i).getNombre();
            gc.fillText(lbl, xs[i] - 20, H - padB + 14);
        }

        return canvas;
    }

    // ═══════════════════════════════════════════════════════════════
    // Gráficas originales mejoradas
    // ═══════════════════════════════════════════════════════════════
    private Canvas buildBarChart() {
        int W = 520, H = 220;
        Canvas canvas = new Canvas(W, H);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        List<Evento> evs = ds.eventos;
        double[] values = evs.stream().mapToDouble(e ->
                ds.compras.stream().filter(c -> c.getEvento().getIdEvento().equals(e.getIdEvento()))
                        .mapToDouble(Compra::getTotal).sum()
        ).toArray();

        double maxVal = Arrays.stream(values).max().orElse(1);
        if (maxVal == 0) maxVal = 1;

        int padL = 10, padB = 50, padT = 10;
        double barW = (W - padL - 20.0) / evs.size();
        double barGap = barW * 0.25;
        double actualBarW = barW - barGap;

        gc.setStroke(Color.web("#21262d")); gc.setLineWidth(1);
        for (int i = 1; i <= 4; i++) {
            double y = padT + (H - padT - padB) * (1 - i / 4.0);
            gc.strokeLine(padL, y, W - 10, y);
            gc.setFill(Color.web("#8b949e")); gc.setFont(javafx.scene.text.Font.font(9));
            gc.fillText(UtilsUI.currency(maxVal * i / 4), 2, y + 4);
        }

        for (int i = 0; i < evs.size(); i++) {
            double ratio = maxVal > 0 ? values[i] / maxVal : 0;
            double barH  = ratio * (H - padT - padB);
            double x = padL + i * barW + barGap / 2;
            double y = padT + (H - padT - padB) - barH;

            Color c = Color.web(PALETTE[i % PALETTE.length]);
            gc.setFill(new LinearGradient(0, y, 0, y + barH, false, CycleMethod.NO_CYCLE,
                    new Stop(0, c), new Stop(1, c.deriveColor(0, 1, 0.5, 0.7))));
            gc.fillRoundRect(x, y, actualBarW, barH, 6, 6);

            gc.setFill(Color.web("#a78bfa")); gc.setFont(javafx.scene.text.Font.font(9));
            if (barH > 16) gc.fillText(UtilsUI.currency(values[i]), x + 2, y - 4);

            gc.setFill(Color.web("#8b949e"));
            String lbl = evs.get(i).getNombre().length() > 12
                    ? evs.get(i).getNombre().substring(0, 11) + "…"
                    : evs.get(i).getNombre();
            gc.fillText(lbl, x, H - padB + 16);
        }
        return canvas;
    }

    private VBox buildDonutChart() {
        Map<EstadoCompra, Long> counts = ds.compras.stream()
                .collect(Collectors.groupingBy(Compra::getEstado, Collectors.counting()));

        int W = 240, H = 220;
        Canvas canvas = new Canvas(W, H);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        double cx = W / 2.0, cy = H / 2.0, outerR = 85, innerR = 50;

        Map<EstadoCompra, Color> colors = Map.of(
                EstadoCompra.PAGADA,      Color.web("#10b981"),
                EstadoCompra.CONFIRMADA,  Color.web("#3b82f6"),
                EstadoCompra.CREADA,      Color.web("#f59e0b"),
                EstadoCompra.CANCELADA,   Color.web("#ef4444"),
                EstadoCompra.REEMBOLSADA, Color.web("#8b949e"),
                EstadoCompra.INCIDENCIA,  Color.web("#a78bfa")
        );

        long total = counts.values().stream().mapToLong(Long::longValue).sum();
        double startAngle = -90;

        for (Map.Entry<EstadoCompra, Long> e : counts.entrySet()) {
            double sweep = 360.0 * e.getValue() / total;
            gc.setFill(colors.getOrDefault(e.getKey(), Color.GRAY));
            gc.fillArc(cx - outerR, cy - outerR, outerR * 2, outerR * 2,
                    startAngle, sweep, javafx.scene.shape.ArcType.ROUND);
            startAngle += sweep;
        }

        gc.setFill(Color.web("#1c2128"));
        gc.fillOval(cx - innerR, cy - innerR, innerR * 2, innerR * 2);
        gc.setFill(Color.web("#e6edf3"));
        gc.setFont(javafx.scene.text.Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 18));
        gc.fillText(String.valueOf(total), cx - 10, cy + 6);
        gc.setFill(Color.web("#8b949e")); gc.setFont(javafx.scene.text.Font.font(10));
        gc.fillText("compras", cx - 18, cy + 20);

        VBox legend = new VBox(6);
        for (Map.Entry<EstadoCompra, Long> e : counts.entrySet()) {
            Color col = colors.getOrDefault(e.getKey(), Color.GRAY);
            HBox row = new HBox(8); row.setAlignment(Pos.CENTER_LEFT);
            Label dot = new Label("●"); dot.setTextFill(col); dot.setStyle("-fx-font-size: 14px;");
            Label lbl = new Label(e.getKey().name() + "  (" + e.getValue() + ")");
            lbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #8b949e;");
            row.getChildren().addAll(dot, lbl);
            legend.getChildren().add(row);
        }

        VBox box = new VBox(12, canvas, legend);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    private VBox buildCategoryBars() {
        Map<String, Long> cats = ds.eventos.stream()
                .collect(Collectors.groupingBy(Evento::getCategoria, Collectors.counting()));
        long max = cats.values().stream().mapToLong(Long::longValue).max().orElse(1);

        VBox box = new VBox(10);
        int i = 0;
        for (Map.Entry<String, Long> e : cats.entrySet()) {
            double ratio = (double) e.getValue() / max;
            String color = PALETTE[i++ % PALETTE.length];
            HBox row = new HBox(12); row.setAlignment(Pos.CENTER_LEFT);
            Label cat = new Label(e.getKey());
            cat.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 12px; -fx-min-width: 80;");
            StackPane barBg = new StackPane();
            barBg.setStyle("-fx-background-color: #161b22; -fx-background-radius: 4;");
            barBg.setPrefHeight(12); HBox.setHgrow(barBg, Priority.ALWAYS);
            Region fill = new Region();
            fill.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 4;");
            fill.setPrefWidth(ratio * 300); fill.setPrefHeight(12);
            barBg.getChildren().add(fill);
            StackPane.setAlignment(fill, Pos.CENTER_LEFT);
            Label cnt = new Label(e.getValue() + " eventos");
            cnt.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 11px; -fx-min-width: 70;");
            row.getChildren().addAll(cat, barBg, cnt);
            box.getChildren().add(row);
        }
        return box;
    }

    private VBox buildTopUsuarios() {
        VBox box = new VBox(8);
        ds.usuarios.stream()
                .sorted((a, b) -> Double.compare(
                        ds.compras.stream().filter(c -> c.getUsuario().getIdUsuario().equals(b.getIdUsuario())).mapToDouble(Compra::getTotal).sum(),
                        ds.compras.stream().filter(c -> c.getUsuario().getIdUsuario().equals(a.getIdUsuario())).mapToDouble(Compra::getTotal).sum()))
                .forEach(u -> {
                    double gasto = ds.compras.stream()
                            .filter(c -> c.getUsuario().getIdUsuario().equals(u.getIdUsuario()))
                            .mapToDouble(Compra::getTotal).sum();
                    long numCompras = ds.compras.stream()
                            .filter(c -> c.getUsuario().getIdUsuario().equals(u.getIdUsuario())).count();

                    HBox row = new HBox(12); row.setAlignment(Pos.CENTER_LEFT);
                    row.setPadding(new Insets(8, 12, 8, 12));
                    row.setStyle("-fx-background-color: -bg-hover; -fx-background-radius: 8;");

                    Label av = new Label(u.getNombre().substring(0, 1).toUpperCase());
                    av.setStyle("-fx-background-color: -accent; -fx-background-radius: 18; " +
                            "-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; " +
                            "-fx-min-width: 36; -fx-min-height: 36; -fx-alignment: center;");
                    VBox info = new VBox(2);
                    Label nm = new Label(u.getNombre()); nm.setStyle("-fx-font-weight: bold; -fx-text-fill: -text-primary;");
                    Label em = new Label(u.getCorreo() + "  ·  " + numCompras + " compras");
                    em.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 11px;");
                    info.getChildren().addAll(nm, em);
                    HBox.setHgrow(info, Priority.ALWAYS);
                    Label total = new Label(UtilsUI.currency(gasto));
                    total.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #10b981;");
                    row.getChildren().addAll(av, info, total);
                    box.getChildren().add(row);
                });
        return box;
    }

    // ── Helpers ───────────────────────────────────────────────────
    private HBox kpi(String label, String value, String delta, String color) {
        VBox box = new VBox(6);
        box.setStyle("-fx-background-color: -bg-card; -fx-background-radius: 12; " +
                "-fx-border-color: -border; -fx-border-radius: 12; -fx-padding: 18 22;");
        HBox.setHgrow(box, Priority.ALWAYS); box.setMaxWidth(Double.MAX_VALUE);
        Label v = new Label(value); v.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        Label l = new Label(label); l.setStyle("-fx-font-size: 11px; -fx-text-fill: #8b949e;");
        boolean pos = delta.startsWith("+");
        Label d = new Label(delta); d.setStyle("-fx-font-size: 11px; -fx-text-fill: " + (pos ? "#10b981" : "#f59e0b") + ";");
        box.getChildren().addAll(v, l, d);
        return new HBox(box) {{ HBox.setHgrow(this, Priority.ALWAYS); setMaxWidth(Double.MAX_VALUE); }};
    }

    private String calcTasaCancelacion() {
        long canceladas = ds.compras.stream().filter(c -> c.getEstado() == EstadoCompra.CANCELADA).count();
        long total = ds.compras.size();
        if (total == 0) return "0%";
        return String.format("%.1f%%", (double) canceladas / total * 100);
    }

    private Label sectionLbl(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: -text-primary;");
        return l;
    }
}