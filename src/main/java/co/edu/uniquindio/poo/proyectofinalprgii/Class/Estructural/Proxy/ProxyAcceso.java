package co.edu.uniquindio.poo.proyectofinalprgii.Class.Estructural.Proxy;

import co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas.Administrador;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas.Usuario;
import co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas.UsuarioFinal;
import co.edu.uniquindio.poo.proyectofinalprgii.UI.DataStore;

import java.util.Optional;

public class ProxyAcceso {

    private static ProxyAcceso instancia;
    private Usuario sesionActual;

    private ProxyAcceso() {}

    public static ProxyAcceso getInstancia() {
        if (instancia == null) instancia = new ProxyAcceso();
        return instancia;
    }

    public Optional<Usuario> login(String correo, String contrasena) {
        DataStore ds = DataStore.get();

        // Buscar en admins
        Optional<Administrador> admin = ds.admins.stream()
                .filter(a -> a.getCorreo().equalsIgnoreCase(correo)
                        && a.getContrasena().equals(contrasena))
                .findFirst();
        if (admin.isPresent()) {
            sesionActual = admin.get();
            sesionActual.iniciarSesion();
            ds.log("🔑 Admin autenticado: " + sesionActual.getNombre());
            return Optional.of(sesionActual);
        }

        // Buscar en usuarios finales
        Optional<UsuarioFinal> usuario = ds.usuarios.stream()
                .filter(u -> u.getCorreo().equalsIgnoreCase(correo)
                        && u.getContrasena().equals(contrasena))
                .findFirst();
        if (usuario.isPresent()) {
            sesionActual = usuario.get();
            sesionActual.iniciarSesion();
            ds.log("🔑 Usuario autenticado: " + sesionActual.getNombre());
            return Optional.of(sesionActual);
        }

        ds.log("⛔ Login fallido para: " + correo);
        return Optional.empty();
    }

    public void logout() {
        if (sesionActual != null)
            DataStore.get().log("👋 Sesión cerrada: " + sesionActual.getNombre());
        sesionActual = null;
    }

    public Usuario getSesionActual()      { return sesionActual; }
    public boolean hayAdmin()             { return sesionActual != null && "ADMIN".equals(sesionActual.getRol()); }
    public boolean hayUsuarioAutenticado(){ return sesionActual != null; }
}