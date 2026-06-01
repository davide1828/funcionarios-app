package com.gestion.funcionarios.model;

/** Catálogo: EstadoEquipo. */
public class EstadoEquipo {
    private int    id;
    private String nombre;
    private String descripcion;
    private boolean activo;

    public EstadoEquipo() {}
    public EstadoEquipo(int id, String nombre, String descripcion, boolean activo) {
        this.id = id; this.nombre = nombre;
        this.descripcion = descripcion; this.activo = activo;
    }

    public int     getId()              { return id; }
    public void    setId(int id)        { this.id = id; }
    public String  getNombre()          { return nombre; }
    public void    setNombre(String n)  { this.nombre = n; }
    public String  getDescripcion()     { return descripcion; }
    public void    setDescripcion(String d) { this.descripcion = d; }
    public boolean isActivo()           { return activo; }
    public void    setActivo(boolean a) { this.activo = a; }

    @Override public String toString()  { return nombre; }
}
