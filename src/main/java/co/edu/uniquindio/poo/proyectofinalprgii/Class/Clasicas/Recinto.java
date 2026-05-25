package co.edu.uniquindio.poo.proyectofinalprgii.Class.Clasicas;

import java.util.ArrayList;

public class Recinto {
    private String idRecinto;
    private String nombre;
    private String direccion;
    private String ciudad;
    private ArrayList<Zona> zonas;

    public Recinto(String idRecinto, String nombre, String direccion, String ciudad) {
        this.idRecinto = idRecinto;
        this.nombre = nombre;
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.zonas = new ArrayList<>();
    }

    public void agregarZona(Zona zona) {
        zonas.add(zona);
    }

    public void eliminarZona(String idZona) {
        zonas.removeIf(z -> z.getIdZona().equals(idZona));
    }

    public String getIdRecinto() { return idRecinto; }
    public String getNombre()    { return nombre; }
    public String getDireccion() { return direccion; }
    public String getCiudad()    { return ciudad; }
    public ArrayList<Zona> getZonas() { return zonas; }
}
