package co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas;

public abstract class Usuario {
    protected String idUsuario;
    protected String nombre;
    protected String correo;
    protected String telefono;
    protected String contrasena;  // NUEVO

    protected Usuario(String idUsuario, String nombre, String correo,
                      String telefono, String contrasena) {
        this.idUsuario  = idUsuario;
        this.nombre     = nombre;
        this.correo     = correo;
        this.telefono   = telefono;
        this.contrasena = contrasena;
    }

    public abstract void iniciarSesion();
    public abstract String getRol();   // "ADMIN" o "USUARIO"

    public void modificarPerfil(String nombre, String correo, String telefono) {
        this.nombre   = nombre;
        this.correo   = correo;
        this.telefono = telefono;
    }

    public String getIdUsuario()  { return idUsuario; }
    public String getNombre()     { return nombre; }
    public String getCorreo()     { return correo; }
    public String getTelefono()   { return telefono; }
    public String getContrasena() { return contrasena; }
    public void setContrasena(String  contrasena) { this.contrasena = contrasena; }
}