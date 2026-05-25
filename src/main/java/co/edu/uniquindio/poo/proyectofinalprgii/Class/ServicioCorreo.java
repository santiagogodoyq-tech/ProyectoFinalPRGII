package co.edu.uniquindio.poo.proyectofinalprgii.Class;

import co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas.Compra;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas.Evento;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas.UsuarioFinal;
import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Envía correos reales usando Gmail SMTP + Jakarta Mail.
 * La configuración se lee de /resources/correo.properties.
 *
 * Los envíos se hacen en hilo aparte para no bloquear la UI.
 */
public class ServicioCorreo {

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Hilo dedicado para no congelar JavaFX
    private static final ExecutorService executor =
            Executors.newSingleThreadExecutor(r -> {
                Thread t = new Thread(r, "correo-thread");
                t.setDaemon(true);
                return t;
            });

    // Configuración cargada desde resources
    private static String REMITENTE;
    private static String CONTRASENA;
    private static String NOMBRE_REMITENTE;
    private static boolean configurado = false;

    static {
        cargarConfiguracion();
    }

    private static void cargarConfiguracion() {
        try (InputStream in = ServicioCorreo.class
                .getClassLoader()
                .getResourceAsStream("correo.properties")) {

            if (in == null) {
                System.err.println("[ServicioCorreo] No se encontró correo.properties — " +
                        "los correos serán simulados en consola.");
                return;
            }

            Properties props = new Properties();
            props.load(in);

            REMITENTE        = props.getProperty("correo.remitente", "").trim();
            CONTRASENA       = props.getProperty("correo.contrasena", "").trim();
            NOMBRE_REMITENTE = props.getProperty("correo.nombre", "EventosPro").trim();

            if (!REMITENTE.isEmpty() && !CONTRASENA.isEmpty()) {
                configurado = true;
                System.out.println("[ServicioCorreo] Configurado: " + REMITENTE);
            } else {
                System.err.println("[ServicioCorreo] correo.properties incompleto — modo simulado.");
            }

        } catch (Exception e) {
            System.err.println("[ServicioCorreo] Error cargando configuración: " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════════
    // API pública
    // ══════════════════════════════════════════════════════════════

    /** Notifica a todos los compradores cuando un evento es cancelado. */
    public static void notificarCancelacionEvento(Evento evento,
                                                  List<Compra> comprasAfectadas) {
        for (Compra compra : comprasAfectadas) {
            UsuarioFinal u = compra.getUsuario();
            String asunto = "❌ Evento cancelado: " + evento.getNombre();
            String cuerpo = buildHtmlCancelacion(evento, compra, u);
            enviarAsync(u.getCorreo(), u.getNombre(), asunto, cuerpo);
        }
    }

    /** Confirma una compra al usuario. */
    public static void notificarConfirmacionCompra(Compra compra) {
        UsuarioFinal u = compra.getUsuario();
        String asunto = "✅ Confirmación de compra " + compra.getIdCompra();
        String cuerpo = buildHtmlConfirmacion(compra, u);
        enviarAsync(u.getCorreo(), u.getNombre(), asunto, cuerpo);
    }

    /** Informa al usuario sobre su reembolso. */
    public static void notificarReembolso(Compra compra, double montoReembolso) {
        UsuarioFinal u = compra.getUsuario();
        String asunto = "↩ Reembolso procesado — " + compra.getIdCompra();
        String cuerpo = buildHtmlReembolso(compra, u, montoReembolso);
        enviarAsync(u.getCorreo(), u.getNombre(), asunto, cuerpo);
    }

    // ══════════════════════════════════════════════════════════════
    // Motor de envío
    // ══════════════════════════════════════════════════════════════

    private static void enviarAsync(String destinatario, String nombreDest,
                                    String asunto, String htmlCuerpo) {
        if (!configurado) {
            // Fallback: imprimir en consola
            System.out.println("╔══ CORREO SIMULADO (sin correo.properties) ══╗");
            System.out.println("  Para:   " + nombreDest + " <" + destinatario + ">");
            System.out.println("  Asunto: " + asunto);
            System.out.println("  (HTML omitido en modo simulado)");
            System.out.println("╚════════════════════════════════════════════╝");
            return;
        }

        executor.submit(() -> {
            try {
                enviarReal(destinatario, nombreDest, asunto, htmlCuerpo);
            } catch (Exception e) {
                System.err.println("[ServicioCorreo] Error enviando a " +
                        destinatario + ": " + e.getMessage());
            }
        });
    }

    private static void enviarReal(String destinatario, String nombreDest,
                                   String asunto, String htmlCuerpo)
            throws MessagingException, jakarta.mail.internet.AddressException, UnsupportedEncodingException {

        // Configuración SMTP Gmail
        Properties smtpProps = new Properties();
        smtpProps.put("mail.smtp.auth",            "true");
        smtpProps.put("mail.smtp.starttls.enable", "true");
        smtpProps.put("mail.smtp.host",            "smtp.gmail.com");
        smtpProps.put("mail.smtp.port",            "587");
        smtpProps.put("mail.smtp.ssl.trust",       "smtp.gmail.com");
        smtpProps.put("mail.smtp.connectiontimeout", "10000");
        smtpProps.put("mail.smtp.timeout",           "10000");

        Session session = Session.getInstance(smtpProps, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(REMITENTE, CONTRASENA);
            }
        });

        // Construir mensaje
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(REMITENTE, NOMBRE_REMITENTE, "UTF-8"));
        msg.addRecipient(Message.RecipientType.TO,
                new InternetAddress(destinatario, nombreDest, "UTF-8"));
        msg.setSubject(asunto, "UTF-8");

        // Multipart: HTML + texto plano de respaldo
        MimeMultipart multipart = new MimeMultipart("alternative");

        // Parte texto plano
        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText(quitarHtml(htmlCuerpo), "UTF-8");

        // Parte HTML
        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(htmlCuerpo, "text/html; charset=UTF-8");

        multipart.addBodyPart(textPart);
        multipart.addBodyPart(htmlPart);
        msg.setContent(multipart);

        Transport.send(msg);
        System.out.println("[ServicioCorreo] ✉ Enviado a " + destinatario
                + " — " + asunto);
    }

    // ══════════════════════════════════════════════════════════════
    // Plantillas HTML
    // ══════════════════════════════════════════════════════════════

    private static String buildHtmlConfirmacion(Compra compra, UsuarioFinal u) {
        String asiento = compra.getEntradas().isEmpty() ? "—"
                : (compra.getEntradas().get(0).getAsiento() != null
                ? compra.getEntradas().get(0).getAsiento().getIdAsiento()
                + " (" + compra.getEntradas().get(0).getAsiento().getSector() + ")"
                : "—");
        String zona = compra.getEntradas().isEmpty() ? "—"
                : compra.getEntradas().get(0).getZona().getNombre();

        return wrap(
                "<h2 style='color:#10b981'>✅ ¡Compra confirmada!</h2>" +
                        "<p>Hola <strong>" + u.getNombre() + "</strong>,</p>" +
                        "<p>Tu compra fue procesada exitosamente.</p>" +
                        tabla(new String[][]{
                                {"ID Compra",  compra.getIdCompra()},
                                {"Evento",     compra.getEvento().getNombre()},
                                {"Fecha",      compra.getFecha().format(FMT)},
                                {"Zona",       zona},
                                {"Asiento",    asiento},
                                {"Total",      String.format("$%,.0f", compra.getTotal())},
                                {"Estado",     "PAGADA ✅"}
                        }) +
                        "<p style='color:#8b949e;font-size:13px'>Presenta este correo o tu QR en la entrada.</p>"
        );
    }

    private static String buildHtmlCancelacion(Evento evento,
                                               Compra compra,
                                               UsuarioFinal u) {
        return wrap(
                "<h2 style='color:#ef4444'>❌ Evento cancelado</h2>" +
                        "<p>Hola <strong>" + u.getNombre() + "</strong>,</p>" +
                        "<p>Lamentamos informarte que el siguiente evento ha sido <strong>cancelado</strong>:</p>" +
                        tabla(new String[][]{
                                {"Evento",       evento.getNombre()},
                                {"Fecha evento", evento.getFecha().format(FMT)},
                                {"Ciudad",       evento.getCiudad()},
                                {"ID Compra",    compra.getIdCompra()},
                                {"Total pagado", String.format("$%,.0f", compra.getTotal())}
                        }) +
                        "<p><strong style='color:#f59e0b'>Tu dinero será reembolsado automáticamente " +
                        "en los próximos 5-7 días hábiles.</strong></p>" +
                        "<p style='color:#8b949e;font-size:13px'>" +
                        "Fecha de cancelación: " + LocalDateTime.now().format(FMT) +
                        "<br>Disculpa los inconvenientes. — EventosPro</p>"
        );
    }

    private static String buildHtmlReembolso(Compra compra,
                                             UsuarioFinal u,
                                             double monto) {
        return wrap(
                "<h2 style='color:#a78bfa'>↩ Reembolso procesado</h2>" +
                        "<p>Hola <strong>" + u.getNombre() + "</strong>,</p>" +
                        "<p>Tu solicitud de reembolso fue procesada.</p>" +
                        tabla(new String[][]{
                                {"ID Compra",       compra.getIdCompra()},
                                {"Evento",          compra.getEvento().getNombre()},
                                {"Monto reembolso", String.format("$%,.0f", monto)},
                                {"Tiempo estimado", "5-7 días hábiles"},
                                {"Estado",          "REEMBOLSADA ↩"}
                        }) +
                        "<p style='color:#8b949e;font-size:13px'>Si tienes dudas contáctanos por este medio.</p>"
        );
    }

    /** Envuelve el contenido en una plantilla HTML con estilo oscuro */
    private static String wrap(String contenido) {
        return "<!DOCTYPE html><html><head><meta charset='UTF-8'></head>" +
                "<body style='margin:0;padding:0;background:#0d1117;font-family:Arial,sans-serif'>" +
                "<table width='100%' cellpadding='0' cellspacing='0' " +
                "style='background:#0d1117;padding:32px 0'><tr><td align='center'>" +
                "<table width='560' cellpadding='0' cellspacing='0' " +
                "style='background:#161b22;border-radius:12px;border:1px solid rgba(255,255,255,0.08)'>" +
                // Header
                "<tr><td style='background:rgba(124,58,237,0.15);padding:24px 32px;" +
                "border-radius:12px 12px 0 0;text-align:center'>" +
                "<span style='font-size:28px'>🎫</span>" +
                "<h1 style='margin:8px 0 0;color:#a78bfa;font-size:20px'>EventosPro</h1>" +
                "</td></tr>" +
                // Body
                "<tr><td style='padding:28px 32px;color:#e6edf3'>" +
                contenido +
                "</td></tr>" +
                // Footer
                "<tr><td style='padding:16px 32px 24px;text-align:center;" +
                "color:#8b949e;font-size:11px;border-top:1px solid rgba(255,255,255,0.07)'>" +
                "Este correo fue enviado automáticamente por EventosPro. " +
                "Por favor no respondas a este mensaje." +
                "</td></tr>" +
                "</table></td></tr></table></body></html>";
    }

    /** Genera una tabla HTML de dos columnas clave-valor */
    private static String tabla(String[][] filas) {
        StringBuilder sb = new StringBuilder(
                "<table width='100%' cellpadding='10' cellspacing='0' " +
                        "style='border-collapse:collapse;margin:16px 0'>"
        );
        for (String[] fila : filas) {
            sb.append("<tr>")
                    .append("<td style='color:#8b949e;font-size:13px;border-bottom:" +
                            "1px solid rgba(255,255,255,0.07);width:160px'>")
                    .append(fila[0]).append("</td>")
                    .append("<td style='color:#e6edf3;font-size:13px;font-weight:bold;" +
                            "border-bottom:1px solid rgba(255,255,255,0.07)'>")
                    .append(fila[1]).append("</td>")
                    .append("</tr>");
        }
        sb.append("</table>");
        return sb.toString();
    }

    /** Elimina etiquetas HTML para el texto plano de respaldo */
    private static String quitarHtml(String html) {
        return html.replaceAll("<[^>]+>", "")
                .replaceAll("&nbsp;", " ")
                .replaceAll("\\s{2,}", "\n")
                .trim();
    }
    public static void notificarCambioEstadoEvento(Evento evento,
                                                   List<Compra> comprasAfectadas,
                                                   String asunto, String mensaje) {
        for (Compra compra : comprasAfectadas) {
            UsuarioFinal u = compra.getUsuario();
            String cuerpo = "<h2>" + asunto + "</h2>" +
                    "<p>Hola <b>" + u.getNombre() + "</b>,</p>" +
                    "<p>" + mensaje + "</p>" +
                    "<p><b>Evento:</b> " + evento.getNombre() + "<br>" +
                    "<b>Ciudad:</b> " + evento.getCiudad() + "</p>" +
                    "<p>— Equipo EventosPro</p>";
            enviarAsync(u.getCorreo(), u.getNombre(), asunto + ": " + evento.getNombre(), cuerpo);
        }
    }
}