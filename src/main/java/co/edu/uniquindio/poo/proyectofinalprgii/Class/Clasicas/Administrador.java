package co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas;

public class Administrador extends Usuario {

    public Administrador(String idUsuario, String nombre,
                         String correo, String telefono, String contrasena) {
        super(idUsuario, nombre, correo, telefono, contrasena);
    }

    @Override public void iniciarSesion() {
        System.out.println("Administrador " + nombre + " inició sesión.");
    }

    @Override public String getRol() { return "ADMIN"; }

    public void gestionarEventos()   { System.out.println("Gestionando eventos..."); }
    public void gestionarUsuarios()  { System.out.println("Gestionando usuarios..."); }
    public void gestionarRecintos()  { System.out.println("Gestionando recintos..."); }
    public void verMetricas()        { System.out.println("Consultando métricas..."); }

    public void registrarIncidencia(Incidencia incidencia) {
        System.out.println("Incidencia registrada: " + incidencia.getTipo());
    }
}