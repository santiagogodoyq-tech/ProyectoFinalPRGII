package co.edu.uniquindio.poo.proyectofinalprgii.UI;

import co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas.*;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.*;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.Comportamental.Observer.ObserverEstado;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.Comportamental.Strategy.PagoEfectivoStrategy;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.Comportamental.Strategy.PagoPSEStrategy;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.Comportamental.Strategy.PagoStrategy;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.Comportamental.Strategy.PagoTarjetaStrategy;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.Creacional.Builder.CompraBuilder;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.Creacional.Singleton.GestorDisponibilidad;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.Enums.EstadoAsiento;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.Enums.EstadoCompra;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDateTime;
import java.util.*;

public class DataStore {

    public static DataStore instance;

    public final ObservableList<Evento>       eventos  = FXCollections.observableArrayList();
    public final ObservableList<Compra>       compras  = FXCollections.observableArrayList();
    public final ObservableList<UsuarioFinal> usuarios = FXCollections.observableArrayList();
    public final ObservableList<Administrador> admins  = FXCollections.observableArrayList();
    public final ObservableList<String>       logLines = FXCollections.observableArrayList();

    public final ServicioMetricas metricas = new ServicioMetricas();
    public final GestorDisponibilidad gestor = GestorDisponibilidad.getInstancia();

    private DataStore() { seed(); }

    public static DataStore get() {
        if (instance == null) instance = new DataStore();
        return instance;
    }

    public int    totalEventos()    { return eventos.size(); }
    public int    totalCompras()    { return compras.size(); }
    public double totalIngresos()   { return compras.stream().mapToDouble(Compra::getTotal).sum(); }
    public long   comprasActivas()  {
        return compras.stream().filter(c ->
                c.getEstado() == EstadoCompra.PAGADA || c.getEstado() == EstadoCompra.CONFIRMADA
        ).count();
    }

    private void seed() {
        Administrador adminRoot = new Administrador(
                "ADM-001", "Administrador", "admin@eventos.com", "300-000-0000", "admin123"
        );
        admins.add(adminRoot);

        ObserverEstado obsLog   = (entidad, id, ant, nuevo) ->
                log("📋 " + entidad + " #" + id + ": " + ant + " → " + nuevo);
        ObserverEstado obsNotif = new co.edu.uniquindio.poo.proyectofinalprgii.Class.Comportamental.Observer.ObserverNotificacion();

        // Recintos
        Recinto r1 = new Recinto("R-001", "Estadio El Campín",    "Cra 30 # 57-60",    "Bogotá");
        Recinto r2 = new Recinto("R-002", "Teatro Metropol",      "Calle 22 # 6-25",   "Bogotá");
        Recinto r3 = new Recinto("R-003", "Plaza Mayor Medellín", "Cra 40 # 50-25",    "Medellín");
        Recinto r4 = new Recinto("R-004", "Centro de Eventos",    "Av. Sexta # 10-30", "Cali");

        Zona z1g = new Zona("Z-001", "General",   5000);
        Zona z1v = new Zona("Z-002", "VIP",        500);
        Zona z2g = new Zona("Z-003", "Platea",     800);
        Zona z3g = new Zona("Z-004", "General",   3000);
        Zona z4v = new Zona("Z-005", "Palco",      200);

        r1.agregarZona(z1g); r1.agregarZona(z1v);
        r2.agregarZona(z2g);
        r3.agregarZona(z3g);
        r4.agregarZona(z4v);

        String[] zonasTipo  = {"VIP",      "PREFERENCIAL", "GENERAL"};
        Zona[]   zonasRef   = {z1v,        z1g,            z3g};
        String[] sectores   = {"ORIENTAL", "CENTRAL",      "OCCIDENTAL"};
        String[] filaLetras = {"A","B","C","D","E","F","G","H","I","J"};

        for (int zi = 0; zi < zonasTipo.length; zi++) {
            Zona zona = zonasRef[zi];
            String tipo = zonasTipo[zi];
            for (String sector : sectores) {
                for (int fila = 0; fila < filaLetras.length; fila++) {
                    for (int col = 1; col <= 5; col++) {
                        String id = tipo.charAt(0) + "-" + sector.charAt(0) +
                                "-" + filaLetras[fila] + col;
                        Asiento a = new Asiento(id, filaLetras[fila], col, sector, tipo);
                        zona.agregarAsiento(a);
                        gestor.registrarAsiento(a);
                    }
                }
            }
        }

        // Eventos — se pasan las políticas reales de reembolso
        Evento e1 = new Evento("EVT-001", "Festival Rock Colombia", "Música",   "El mejor festival del rock nacional", "Bogotá",   LocalDateTime.now().plusDays(15), "Sin reembolso si faltan menos de 48h para el evento.",               r1);
        Evento e2 = new Evento("EVT-002", "Concierto Sinfónico",    "Clásica",  "Orquesta Filarmónica de Bogotá",     "Bogotá",   LocalDateTime.now().plusDays(30), "Reembolso del 100% si solicitas con más de 7 días de anticipación.", r2);
        Evento e3 = new Evento("EVT-003", "Feria de las Flores",    "Cultural", "Evento cultural más importante",     "Medellín", LocalDateTime.now().plusDays(45), "Sin reembolso una vez confirmada la compra.",                        r3);
        Evento e4 = new Evento("EVT-004", "Festival de Salsa",      "Música",   "Cali se viste de salsa",             "Cali",     LocalDateTime.now().plusDays(60), "Reembolso del 50% si solicitas con más de 3 días de anticipación.",  r4);
        Evento e5 = new Evento("EVT-005", "Expo Tecnología 2025",   "Tech",     "Innovación y tecnología",            "Bogotá",   LocalDateTime.now().plusDays(90), "Sin reembolso.",                                                     r1);

        e1.agregarObservador(obsLog); e1.agregarObservador(obsNotif); e1.publicar();
        e2.agregarObservador(obsLog); e2.agregarObservador(obsNotif); e2.publicar();
        e3.agregarObservador(obsLog); e3.agregarObservador(obsNotif); e3.publicar();
        e4.agregarObservador(obsLog); e4.agregarObservador(obsNotif);
        e5.agregarObservador(obsLog); e5.agregarObservador(obsNotif); e5.publicar();

        eventos.addAll(e1, e2, e3, e4, e5);

        UsuarioFinal u1 = new UsuarioFinal("U-001", "Sara Benjumea",     "sbenjumeagallego@gmail.com", "310-111-2222", "sara123");
        UsuarioFinal u2 = new UsuarioFinal("U-002", "Santiago Godoy",    "Santiago@mail.com",          "315-333-4444", "santiago123");
        UsuarioFinal u3 = new UsuarioFinal("U-003", "Valentina Torres",  "vale@mail.com",              "320-555-6666", "vale123");

        MetodoPago mp1 = new MetodoPago("MP-001", "TARJETA_CREDITO", "**** 4321");
        MetodoPago mp2 = new MetodoPago("MP-002", "PSE",             "Bancolombia");
        MetodoPago mp3 = new MetodoPago("MP-003", "EFECTIVO",        "Efecty");
        u1.agregarMetodoPago(mp1);
        u2.agregarMetodoPago(mp2);
        u3.agregarMetodoPago(mp3);

        usuarios.addAll(u1, u2, u3);

        // ── BUGFIX precios: los valores seed ahora usan precio real del asiento ──
        // Asiento VIP = 246 000 base  → PrecioEntrada(246000, 246000*0.18, 246000*0.06)
        // Asiento PREFERENCIAL = 124 000 base
        // Asiento GENERAL = 62 000 base
        crearCompra("COMP-001", u1, e1, z1g, new PrecioEntrada(62_000,  11_160, 3_720),  mp1, new PagoTarjetaStrategy(), EstadoCompra.PAGADA);
        crearCompra("COMP-002", u2, e2, z2g, new PrecioEntrada(62_000,  11_160, 3_720),  mp2, new PagoPSEStrategy(),     EstadoCompra.PAGADA);
        crearCompra("COMP-003", u1, e3, z3g, new PrecioEntrada(62_000,  11_160, 3_720),  mp1, new PagoTarjetaStrategy(), EstadoCompra.CONFIRMADA);
        crearCompra("COMP-004", u3, e1, z1v, new PrecioEntrada(246_000, 44_280, 14_760), mp3, new PagoEfectivoStrategy(),EstadoCompra.CREADA);
        crearCompra("COMP-005", u2, e4, z4v, new PrecioEntrada(62_000,  11_160, 3_720),  mp2, new PagoPSEStrategy(),     EstadoCompra.CANCELADA);

        log("🚀 Sistema iniciado con " + eventos.size() + " eventos y " + compras.size() + " compras");
    }

    private void crearCompra(String id, UsuarioFinal usuario, Evento evento,
                             Zona zona, PrecioEntrada precio, MetodoPago metodo,
                             PagoStrategy estrategia, EstadoCompra estado) {
        // Toma el primer asiento disponible de la zona
        Asiento asiento = zona.getAsientos().stream()
                .filter(a -> a.getEstado() == EstadoAsiento.DISPONIBLE)
                .findFirst()
                .orElse(zona.getAsientos().isEmpty() ? null : zona.getAsientos().get(0));

        Entrada entrada = new Entrada("ENT-" + id, zona, asiento, precio);

        // ── BUGFIX: marcar el asiento como VENDIDO en datos semilla ──
        if (asiento != null && estado != EstadoCompra.CANCELADA && estado != EstadoCompra.REEMBOLSADA) {
            asiento.cambiarEstado(EstadoAsiento.VENDIDO);
            gestor.reservar(asiento.getIdAsiento());
        }

        Compra c = new CompraBuilder()
                .conUsuario(usuario)
                .conEvento(evento)
                .conEntrada(entrada)
                .conEstrategiaPago(estrategia)
                .build();

        c.setEstadoEnum(estado);

        if (estado == EstadoCompra.PAGADA || estado == EstadoCompra.CONFIRMADA) {
            Pago pago = new Pago("PAG-" + id, entrada.getPrecioFinal(),
                    LocalDateTime.now(),
                    "REF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                    metodo);
            c.setPago(pago);
        }
        compras.add(c);
    }

    public void log(String msg) {
        String ts = java.time.LocalTime.now().toString().substring(0, 8);
        logLines.add(0, "[" + ts + "] " + msg);
        if (logLines.size() > 80) logLines.remove(logLines.size() - 1);
    }

    public Evento crearEvento(String nombre, String categoria, String descripcion,
                              String ciudad, LocalDateTime fecha,
                              double precioGeneral, double precioPreferencial, double precioVIP) {
        String rid = "R-NEW-" + (eventos.size() + 1);
        Recinto r = new Recinto(rid, "Recinto " + ciudad, "Por definir", ciudad);

        String[] tipos    = {"VIP", "PREFERENCIAL", "GENERAL"};
        double[] precios  = {precioVIP, precioPreferencial, precioGeneral};
        String[] nomZonas = {"VIP", "Preferencial", "General"};
        String[] sectores = {"ORIENTAL", "CENTRAL", "OCCIDENTAL"};
        String[] filas    = {"A","B","C","D","E","F","G","H","I","J"};

        for (int zi = 0; zi < tipos.length; zi++) {
            Zona z = new Zona("Z-" + rid + "-" + zi, nomZonas[zi], 500);
            for (String sector : sectores) {
                for (String fila : filas) {
                    for (int col = 1; col <= 5; col++) {
                        String aid = tipos[zi].charAt(0) + "-" + sector.charAt(0) + "-" + fila + col + "-" + (eventos.size() + 1);
                        Asiento a = new Asiento(aid, fila, col, sector, tipos[zi]);
                        a.setPrecio(precios[zi]);
                        z.agregarAsiento(a);
                        gestor.registrarAsiento(a);
                    }
                }
            }
            r.agregarZona(z);
        }

        String id = "EVT-" + String.format("%03d", eventos.size() + 1);
        Evento e = new Evento(id, nombre, categoria, descripcion, ciudad, fecha,
                "Reembolso según política general: >7 días 100%, 2-6 días 75%, <2 días 20%.", r);
        e.agregarObservador((ent, eid, ant, nvo) -> log("📋 " + ent + " #" + eid + ": " + ant + " → " + nvo));
        e.agregarObservador(new co.edu.uniquindio.poo.proyectofinalprgii.Class.Comportamental.Observer.ObserverNotificacion());
        e.publicar();
        eventos.add(e);
        log("✅ Evento creado: " + nombre);
        return e;
    }

    // ── BUGFIX precio: usa getPrecioSegunTipo() del asiento (que respeta setPrecio) ──
    public Compra nuevaCompra(UsuarioFinal usuario, Evento evento,
                              Asiento asiento,
                              PagoStrategy estrategia,
                              MetodoPago metodoPago) {
        Zona zona = evento.getRecinto().getZonas().stream()
                .filter(z -> z.getAsientos().contains(asiento))
                .findFirst()
                .orElse(evento.getRecinto().getZonas().isEmpty()
                        ? new Zona("Z-TMP", "General", 100)
                        : evento.getRecinto().getZonas().get(0));

        // ── Precio viene del asiento, no de un valor fijo ──
        double base = asiento.getPrecioSegunTipo();
        double impuesto = base * 0.18;
        double comision = base * 0.06;
        PrecioEntrada precio = new PrecioEntrada(base, impuesto, comision);

        String entId = "ENT-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        Entrada entrada = new Entrada(entId, zona, asiento, precio);

        // ── BUGFIX: marcar asiento como VENDIDO al comprar ──
        asiento.cambiarEstado(EstadoAsiento.VENDIDO);
        gestor.reservar(asiento.getIdAsiento());

        Compra c = new CompraBuilder()
                .conUsuario(usuario)
                .conEvento(evento)
                .conEntrada(entrada)
                .conEstrategiaPago(estrategia)
                .build();

        c.setEstadoEnum(EstadoCompra.CREADA);
        compras.add(c);

        log("🛒 Compra creada: " + c.getIdCompra()
                + " — " + asiento.getIdAsiento()
                + " — " + usuario.getNombre()
                + " — $" + String.format("%,.0f", entrada.getPrecioFinal()));
        return c;
    }

    public boolean eliminarUsuario(String idUsuario) {
        boolean eliminado = usuarios.removeIf(u -> u.getIdUsuario().equals(idUsuario));
        if (eliminado) {
            compras.removeIf(c -> c.getUsuario().getIdUsuario().equals(idUsuario));
            log("🗑 Usuario eliminado: " + idUsuario);
        }
        return eliminado;
    }

    public boolean registrarUsuario(String nombre, String correo,
                                    String telefono, String contrasena) {
        boolean correoOcupado = usuarios.stream()
                .anyMatch(u -> u.getCorreo().equalsIgnoreCase(correo))
                || admins.stream()
                .anyMatch(a -> a.getCorreo().equalsIgnoreCase(correo));
        if (correoOcupado) return false;

        String id = "U-" + String.format("%03d", usuarios.size() + 1);
        UsuarioFinal nuevo = new UsuarioFinal(id, nombre, correo, telefono, contrasena);
        usuarios.add(nuevo);
        log("✅ Nuevo usuario registrado: " + nombre);
        return true;
    }
}