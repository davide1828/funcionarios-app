package com.gestion.funcionarios.model;

/**
 * Municipio o ciudad, asociado a un departamento.
 */
public class Municipio {

    private int          id;
    private String       codigo;
    private String       nombre;
    private Departamento departamento;
    private boolean      activo;

    public Municipio() {}

    public Municipio(int id, String codigo, String nombre,
                     Departamento departamento, boolean activo) {
        this.id           = id;
        this.codigo       = codigo;
        this.nombre       = nombre;
        this.departamento = departamento;
        this.activo       = activo;
    }

    public int          getId()            { return id; }
    public void         setId(int id)      { this.id = id; }

    public String       getCodigo()        { return codigo; }
    public void         setCodigo(String c){ this.codigo = c; }

    public String       getNombre()        { return nombre; }
    public void         setNombre(String n){ this.nombre = n; }

    public Departamento getDepartamento()  { return departamento; }
    public void         setDepartamento(Departamento d) { this.departamento = d; }

    public boolean      isActivo()         { return activo; }
    public void         setActivo(boolean activo) { this.activo = activo; }

    @Override
    public String toString() {
        return nombre + (departamento != null ? " (" + departamento.getNombre() + ")" : "");
    }
}
