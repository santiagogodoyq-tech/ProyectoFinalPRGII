package co.edu.uniquindio.poo.proyectofinalprgii.UI;

import co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas.Usuario;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.Estructural.Proxy.ProxyAcceso;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

import java.util.Optional;
import java.util.function.Consumer;


public class LoginUI {

    private final Consumer<Usuario> onLoginExitoso;

    public LoginUI(Consumer<Usuario> onLoginExitoso) {
        this.onLoginExitoso = onLoginExitoso;
    }

    public Node build() {
        // Fondo dividido
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #0d1117;");

        // Panel central
        VBox panel = new VBox(0);
        panel.setMaxWidth(420);
        panel.setStyle(
                "-fx-background-color: #161b22;" +
                        "-fx-background-radius: 16;" +
                        "-fx-border-color: rgba(255,255,255,0.08);" +
                        "-fx-border-radius: 16;" +
                        "-fx-border-width: 1;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 40, 0, 0, 8);"
        );

        // ── Header del panel ──────────────────────────────────────
        VBox header = new VBox(8);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(36, 32, 24, 32));
        header.setStyle("-fx-background-color: rgba(124,58,237,0.12); -fx-background-radius: 16 16 0 0;");

        Label icon = new Label("🎫");
        icon.setStyle("-fx-font-size: 36px;");

        Label title = new Label("EventosPro");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #a78bfa;");

        Label sub = new Label("Inicia sesión para continuar");
        sub.setStyle("-fx-font-size: 13px; -fx-text-fill: #8b949e;");

        header.getChildren().addAll(icon, title, sub);

        // ── Formulario ────────────────────────────────────────────
        VBox form = new VBox(16);
        form.setPadding(new Insets(28, 32, 32, 32));

        // Correo
        VBox correoBox = new VBox(6);
        Label correoLbl = new Label("Correo electrónico");
        correoLbl.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 12px;");
        TextField correoField = new TextField();
        correoField.setPromptText("correo@ejemplo.com");
        correoField.setStyle(fieldStyle());
        correoBox.getChildren().addAll(correoLbl, correoField);

        // Contraseña
        VBox passBox = new VBox(6);
        Label passLbl = new Label("Contraseña");
        passLbl.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 12px;");
        PasswordField passField = new PasswordField();
        passField.setPromptText("••••••••");
        passField.setStyle(fieldStyle());
        passBox.getChildren().addAll(passLbl, passField);

        // Mensaje de error
        Label errorLbl = new Label("");
        errorLbl.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 12px;");
        errorLbl.setWrapText(true);
        errorLbl.setTextAlignment(TextAlignment.CENTER);
        errorLbl.setVisible(false);

        // Botón login
        Button btnLogin = new Button("Iniciar Sesión");
        btnLogin.setMaxWidth(Double.MAX_VALUE);
        btnLogin.setStyle(
                "-fx-background-color: #7c3aed; -fx-text-fill: white; " +
                        "-fx-font-weight: bold; -fx-font-size: 14px; " +
                        "-fx-background-radius: 8; -fx-padding: 12 0; -fx-cursor: hand;"
        );

        btnLogin.setOnMouseEntered(e ->
                btnLogin.setStyle(btnLogin.getStyle().replace("#7c3aed", "#6d28d9")));
        btnLogin.setOnMouseExited(e ->
                btnLogin.setStyle(btnLogin.getStyle().replace("#6d28d9", "#7c3aed")));
        // Button Registro
        Button btnRegistro = new Button("¿No tienes cuenta? Regístrate");
        btnRegistro.setMaxWidth(Double.MAX_VALUE);
        btnRegistro.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: #a78bfa; " +
                        "-fx-font-size: 12px; -fx-cursor: hand; -fx-underline: true; -fx-padding: 4 0;");
        btnRegistro.setOnAction(e -> mostrarRegistro());
        // Acción login
        Runnable doLogin = () -> {
            String correo = correoField.getText().trim();
            String pass   = passField.getText();

            if (correo.isEmpty() || pass.isEmpty()) {
                errorLbl.setText("Por favor completa todos los campos.");
                errorLbl.setVisible(true);
                return;
            }

            Optional<Usuario> resultado = ProxyAcceso.getInstancia().login(correo, pass);
            if (resultado.isPresent()) {
                errorLbl.setVisible(false);
                onLoginExitoso.accept(resultado.get());
            } else {
                errorLbl.setText("Correo o contraseña incorrectos.");
                errorLbl.setVisible(true);
                passField.clear();
                correoField.requestFocus();
            }
        };

        btnLogin.setOnAction(e -> doLogin.run());
        passField.setOnAction(e -> doLogin.run());
        correoField.setOnAction(e -> passField.requestFocus());

        // Hint de cuentas demo
        VBox hint = new VBox(4);
        hint.setAlignment(Pos.CENTER);
        hint.setPadding(new Insets(12, 0, 0, 0));
        hint.setStyle(
                "-fx-background-color: rgba(124,58,237,0.08);" +
                        "-fx-background-radius: 8; -fx-padding: 10 14;"
        );
        Label hintTitle = new Label("Cuentas de prueba");
        hintTitle.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 11px; -fx-font-weight: bold;");
        Label hintAdmin = new Label("Admin:   admin@eventos.com  /  admin123");
        hintAdmin.setStyle("-fx-text-fill: #a78bfa; -fx-font-size: 11px; -fx-font-family: monospace;");
        Label hintUser  = new Label("Usuario: sara@mail.com  /  sara123");
        hintUser.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 11px; -fx-font-family: monospace;");
        hint.getChildren().addAll(hintTitle, hintAdmin, hintUser);

        
        form.getChildren().addAll(correoBox, passBox, errorLbl, btnLogin, btnRegistro, hint);
        panel.getChildren().addAll(header, form);

        StackPane.setAlignment(panel, Pos.CENTER);
        root.getChildren().add(panel);
        return root;
    }


    // Y agrega mostrarRegistro() en la misma clase:
    private void mostrarRegistro() {
        Dialog<Void> dlg = new Dialog<>();
        dlg.setTitle("Crear cuenta");
        dlg.setHeaderText(null);
        dlg.getDialogPane().setStyle("-fx-background-color: #1c2128; -fx-min-width: 400;");

        VBox form = new VBox(14);
        form.setPadding(new Insets(24));

        Label titulo = new Label("✨  Crear cuenta nueva");
        titulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #a78bfa;");

        TextField fNombre = rf("Nombre completo");
        TextField fCorreo = rf("correo@ejemplo.com");
        TextField fTel    = rf("310-000-0000");
        PasswordField fPass = new PasswordField(); fPass.setPromptText("Contraseña");
        fPass.setStyle(fieldStyle());
        PasswordField fPass2 = new PasswordField(); fPass2.setPromptText("Confirmar contraseña");
        fPass2.setStyle(fieldStyle());

        Label errReg = new Label("");
        errReg.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 12px;");
        errReg.setVisible(false);

        form.getChildren().addAll(titulo,
                rl("Nombre"), fNombre,
                rl("Correo"), fCorreo,
                rl("Teléfono"), fTel,
                rl("Contraseña"), fPass,
                rl("Confirmar contraseña"), fPass2,
                errReg);

        dlg.getDialogPane().setContent(form);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Button okBtn = (Button) dlg.getDialogPane().lookupButton(ButtonType.OK);
        okBtn.setText("Crear Cuenta");

        dlg.setResultConverter(bt -> {
            if (bt != ButtonType.OK) return null;
            if (fNombre.getText().isBlank() || fCorreo.getText().isBlank()
                    || fPass.getText().isBlank()) {
                errReg.setText("Completa todos los campos.");
                errReg.setVisible(true);
                return null;
            }
            if (!fPass.getText().equals(fPass2.getText())) {
                errReg.setText("Las contraseñas no coinciden.");
                errReg.setVisible(true);
                return null;
            }
            boolean ok2 = DataStore.get().registrarUsuario(
                    fNombre.getText().trim(), fCorreo.getText().trim(),
                    fTel.getText().trim(), fPass.getText());
            if (!ok2) {
                errReg.setText("Ese correo ya está registrado.");
                errReg.setVisible(true);
                return null;
            }
            Alert exito = new Alert(Alert.AlertType.INFORMATION,
                    "¡Cuenta creada! Ya puedes iniciar sesión.");
            exito.setHeaderText(null);
            exito.showAndWait();
            return null;
        });
        dlg.showAndWait();
    }

    private TextField rf(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle(fieldStyle());
        return tf;
    }

    private Label rl(String t) {
        Label l = new Label(t);
        l.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 12px;");
        return l;
    }
    private String fieldStyle() {
        return "-fx-background-color: #0d1117; -fx-text-fill: #e6edf3; " +
                "-fx-prompt-text-fill: #8b949e; -fx-border-color: rgba(255,255,255,0.08); " +
                "-fx-border-radius: 8; -fx-background-radius: 8; " +
                "-fx-padding: 10 12; -fx-font-size: 13px;";
    }
}