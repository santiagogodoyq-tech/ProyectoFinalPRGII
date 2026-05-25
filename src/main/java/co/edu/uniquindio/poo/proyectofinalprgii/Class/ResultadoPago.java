package co.edu.uniquindio.poo.proyectofinalprgii.Class;

public class ResultadoPago {
    private boolean exitoso;
    private String referencia;
    private String mensaje;

    public ResultadoPago(boolean exitoso, String referencia, String mensaje) {
        this.exitoso = exitoso;
        this.referencia = referencia;
        this.mensaje = mensaje;
    }

    public boolean isExitoso()    { return exitoso; }
    public String getReferencia() { return referencia; }
    public String getMensaje()    { return mensaje; }
}
