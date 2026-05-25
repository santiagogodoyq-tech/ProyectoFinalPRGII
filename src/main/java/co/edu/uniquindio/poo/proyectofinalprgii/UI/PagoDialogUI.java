package co.edu.uniquindio.poo.proyectofinalprgii.UI;

import co.edu.uniquindio.poo.proyectofinalprgii.Class.Comportamental.Strategy.*;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.MetodoPago;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.Optional;
import java.util.function.BiConsumer;

/**
 * Diálogo para elegir método de pago antes de confirmar una compra.
 * Retorna la PagoStrategy elegida y el MetodoPago creado.
 */
public class PagoDialogUI {

    // 1. Primero el record (clase anidada estática)
    // PONER:
    public record ResultadoEleccion(PagoStrategy estrategia, MetodoPago metodoPago,
                                    boolean pagar, boolean conMerchandising,
                                    boolean conSeguro, boolean conParqueadero) {}

    // 2. Interfaz funcional anidada
    @FunctionalInterface
    public interface QuintConsumer {
        void accept(PagoStrategy e, MetodoPago m, boolean pagar, boolean merch, boolean seguro, boolean parqueadero);
    }

    // 3. Campos de instancia
    private final double monto;
    private final QuintConsumer onConfirmado;

    // 4. Constructor
    public PagoDialogUI(double monto, QuintConsumer onConfirmado) {
        this.monto = monto;
        this.onConfirmado = onConfirmado;
    }

    public void mostrar() {
        Dialog<ResultadoEleccion> dlg = new Dialog<>();
        dlg.setTitle("Método de Pago");
        dlg.setHeaderText(null);
        dlg.getDialogPane().setStyle("-fx-background-color: #161b22; -fx-min-width: 420;");

        VBox contenido = new VBox(18);
        contenido.setPadding(new Insets(24));

        Label titulo = new Label("💳  Elige cómo pagar");
        titulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #a78bfa;");

        ToggleGroup grupo = new ToggleGroup();
        RadioButton rbTarjeta  = radio("💳  Tarjeta de Crédito/Débito", grupo);
        RadioButton rbPSE      = radio("🏦  PSE — Débito bancario",     grupo);
        RadioButton rbEfectivo = radio("💵  Efectivo (cupón de pago)",   grupo);
        rbTarjeta.setSelected(true);

        TextField fNumTarjeta  = field("Número de tarjeta (ej: 4111 1111 1111 1111)");
        TextField fVencimiento = field("Vencimiento (MM/AA)");
        TextField fCVV         = field("CVV");
        VBox camposTarjeta = new VBox(8, lbl("Número"), fNumTarjeta,
                lbl("Vencimiento"), fVencimiento, lbl("CVV"), fCVV);

        ComboBox<String> cbBanco = new ComboBox<>();
        cbBanco.getItems().addAll("Bancolombia", "Davivienda", "Banco de Bogotá",
                "BBVA", "Nequi", "Daviplata");
        cbBanco.setValue("Bancolombia");
        cbBanco.setMaxWidth(Double.MAX_VALUE);
        VBox camposPSE = new VBox(8, lbl("Banco"), cbBanco);
        camposPSE.setVisible(false);

        Label lblEfectivo = new Label("Se generará un cupón de pago para pagar en\n" +
                "corresponsales bancarios (Efecty, Baloto).");
        lblEfectivo.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 12px;");
        VBox camposEfectivo = new VBox(8, lblEfectivo);
        camposEfectivo.setVisible(false);

        rbTarjeta.setOnAction(e -> { camposTarjeta.setVisible(true);  camposPSE.setVisible(false); camposEfectivo.setVisible(false); });
        rbPSE.setOnAction(e ->      { camposTarjeta.setVisible(false); camposPSE.setVisible(true);  camposEfectivo.setVisible(false); });
        rbEfectivo.setOnAction(e -> { camposTarjeta.setVisible(false); camposPSE.setVisible(false); camposEfectivo.setVisible(true);  });

        VBox camposDinamicos = new VBox(10, camposTarjeta, camposPSE, camposEfectivo);

        // Extras / decoradores
        Label extrasLbl = new Label("Extras opcionales:");
        extrasLbl.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 12px; -fx-font-weight: bold;");
        // PONER:
        CheckBox chkMerchandising = new CheckBox("🎁  Kit Merchandising  (+$25,000)");
        CheckBox chkSeguro        = new CheckBox("🛡  Seguro de Vida  (+$15,000)");
        CheckBox chkParqueadero   = new CheckBox("🚗  Parqueadero  (+$10,000)");
        chkParqueadero.setStyle("-fx-text-fill: #e6edf3;");
        chkMerchandising.setStyle("-fx-text-fill: #e6edf3;");
        chkSeguro.setStyle("-fx-text-fill: #e6edf3;");

        Label totalDinamico = new Label(String.format("Total a pagar: $%,.0f", monto));
        totalDinamico.setStyle("-fx-font-size: 14px; -fx-text-fill: #10b981; -fx-font-weight: bold;");
        // PONER:
        Runnable actualizarTotal = () -> {
            double extra = (chkMerchandising.isSelected() ? 25_000 : 0)
                    + (chkSeguro.isSelected()        ? 15_000 : 0)
                    + (chkParqueadero.isSelected()   ? 10_000 : 0);
            totalDinamico.setText(String.format("Total a pagar: $%,.0f", monto + extra));
        };
        chkMerchandising.setOnAction(e -> actualizarTotal.run());
        chkSeguro.setOnAction(e -> actualizarTotal.run());
        chkParqueadero.setOnAction(e -> actualizarTotal.run());

        contenido.getChildren().addAll(titulo, rbTarjeta, rbPSE, rbEfectivo,
                camposDinamicos, extrasLbl, chkMerchandising, chkSeguro, chkParqueadero, totalDinamico);

        // UNA sola vez: setContent y getButtonTypes
        dlg.getDialogPane().setContent(contenido);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL, ButtonType.APPLY);

        // PONER:
        Button okBtn   = (Button) dlg.getDialogPane().lookupButton(ButtonType.OK);
        Button pendBtn = (Button) dlg.getDialogPane().lookupButton(ButtonType.APPLY);
        okBtn.setText("Confirmar Pago");
        pendBtn.setText("Dejar sin pagar");
        okBtn.getStyleClass().add("btn-primary");

        // Validar tarjeta en tiempo real
        okBtn.setDisable(fNumTarjeta.getText().isBlank());
        fNumTarjeta.textProperty().addListener((o, a, b) ->
                okBtn.setDisable(rbTarjeta.isSelected() && b.isBlank()));
        rbTarjeta.setOnAction(e -> {
            camposTarjeta.setVisible(true); camposPSE.setVisible(false); camposEfectivo.setVisible(false);
            okBtn.setDisable(fNumTarjeta.getText().isBlank());
        });
        rbPSE.setOnAction(e -> {
            camposTarjeta.setVisible(false); camposPSE.setVisible(true); camposEfectivo.setVisible(false);
            okBtn.setDisable(false);
        });
        rbEfectivo.setOnAction(e -> {
            camposTarjeta.setVisible(false); camposPSE.setVisible(false); camposEfectivo.setVisible(true);
            okBtn.setDisable(false);
        });

        dlg.setResultConverter(bt -> {
            if (bt == ButtonType.CANCEL) return null;
            PagoStrategy estrategia;
            MetodoPago mp;
            if (bt == ButtonType.APPLY) {
                return new ResultadoEleccion(new PagoTarjetaStrategy(),
                        new MetodoPago("MP-PEND", "PENDIENTE", "Sin pagar"),
                        false, chkMerchandising.isSelected(), chkSeguro.isSelected(), chkParqueadero.isSelected());
            }
            if (rbTarjeta.isSelected()) {
                String det = fNumTarjeta.getText().isBlank() ? "****"
                        : "**** " + fNumTarjeta.getText().replaceAll("\\s", "")
                        .substring(Math.max(0, fNumTarjeta.getText().length() - 4));
                estrategia = new PagoTarjetaStrategy();
                mp = new MetodoPago("MP-TC", "TARJETA_CREDITO", det);
            } else if (rbPSE.isSelected()) {
                estrategia = new PagoPSEStrategy();
                mp = new MetodoPago("MP-PSE", "PSE", cbBanco.getValue());
            } else {
                estrategia = new PagoEfectivoStrategy();
                mp = new MetodoPago("MP-EFE", "EFECTIVO", "Efecty/Baloto");
            }
            return new ResultadoEleccion(estrategia, mp, true,
                    chkMerchandising.isSelected(), chkSeguro.isSelected(), chkParqueadero.isSelected());
        });


        dlg.showAndWait().ifPresent(r ->
                onConfirmado.accept(r.estrategia(), r.metodoPago(),
                        r.pagar(), r.conMerchandising(), r.conSeguro(), r.conParqueadero()));
    }

    private RadioButton radio(String texto, ToggleGroup grupo) {
        RadioButton rb = new RadioButton(texto);
        rb.setToggleGroup(grupo);
        rb.setStyle("-fx-text-fill: #e6edf3; -fx-font-size: 13px;");
        return rb;
    }

    private TextField field(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle("-fx-background-color: #0d1117; -fx-text-fill: #e6edf3; " +
                "-fx-prompt-text-fill: #8b949e; -fx-border-color: rgba(255,255,255,0.1); " +
                "-fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 8 10;");
        return tf;
    }

    private Label lbl(String t) {
        Label l = new Label(t);
        l.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 11px;");
        return l;
    }
}