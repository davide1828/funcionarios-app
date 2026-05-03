package com.gestion.funcionarios.model;

/**
 * División política territorial (departamento del país).
 */
public class Departamento {

    private int     id;
    private String  codigo;
    private String  nombre;
    private boolean activo;

    public Departamento() {}

    public Departamento(int id, String codigo, String nombre, boolean activo) {
        this.id     = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.activo = activo;
    }

    public int     getId()     { return id; }
    public void    setId(int id) { this.id = id; }

    public String  getCodigo() { return codigo; }
    public void    setCodigo(String codigo) { this.codigo = codigo; }

    public String  getNombre() { return nombre; }
    public void    setNombre(String nombre) { this.nombre = nombre; }

    public boolean isActivo()  { return activo; }
    public void    setActivo(boolean activo) { this.activo = activo; }

    @Override
    public String toString() { return nombre; }
}
