package com.gestion.funcionarios.model;

/**
 * Cargo que puede ocupar un funcionario dentro de un área.
 */
public class Cargo {

    private int     id;
    private String  nombre;
    private String  nivelSalarial;
    private Area    area;
    private boolean activo;

    public Cargo() {}

    public Cargo(int id, String nombre, String nivelSalarial,
                 Area area, boolean activo) {
        this.id            = id;
        this.nombre        = nombre;
        this.nivelSalarial = nivelSalarial;
        this.area          = area;
        this.activo        = activo;
    }

    public int     getId()              { return id; }
    public void    setId(int id)        { this.id = id; }

    public String  getNombre()          { return nombre; }
    public void    setNombre(String n)  { this.nombre = n; }

    public String  getNivelSalarial()   { return nivelSalarial; }
    public void    setNivelSalarial(String ns) { this.nivelSalarial = ns; }

    public Area    getArea()            { return area; }
    public void    setArea(Area area)   { this.area = area; }

    public boolean isActivo()           { return activo; }
    public void    setActivo(boolean a) { this.activo = a; }

    @Override
    public String toString() {
        return nombre + (area != null ? " – " + area.getNombre() : "");
    }
}
