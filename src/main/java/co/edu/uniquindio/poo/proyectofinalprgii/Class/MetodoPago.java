package co.edu.uniquindio.poo.proyectofinalprgii.Class;

public class MetodoPago {

    private String idMetodo;
    private String tipo;
    private String detalle;

    public MetodoPago(String idMetodo, String tipo, String detalle) {
        this.idMetodo = idMetodo;
        this.tipo = tipo;
        this.detalle = detalle;
    }

    public String getIdMetodo() { return idMetodo; }
    public String getTipo()     { return tipo; }
    public String getDetalle()  { return detalle; }
}
