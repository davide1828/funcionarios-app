package com.gestion.funcionarios.model;

/**
 * Representa el tipo de documento de identidad de un funcionario.
 * Ejemplo: Cédula de Ciudadanía, Pasaporte, Cédula de Extranjería, etc.
 */
public class TipoDocumento {

    private int     id;
    private String  codigo;
    private String  nombre;
    private boolean activo;

    public TipoDocumento() {}

    public TipoDocumento(int id, String codigo, String nombre, boolean activo) {
        this.id     = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.activo = activo;
    }

    // ── Getters / Setters ──────────────────────────────────────────────────
    public int     getId()     { return id; }
    public void    setId(int id) { this.id = id; }

    public String  getCodigo() { return codigo; }
    public void    setCodigo(String codigo) { this.codigo = codigo; }

    public String  getNombre() { return nombre; }
    public void    setNombre(String nombre) { this.nombre = nombre; }

    public boolean isActivo()  { return activo; }
    public void    setActivo(boolean activo) { this.activo = activo; }

    /** Representación usada por JComboBox. */
    @Override
    public String toString() {
        return codigo + " – " + nombre;
    }
}
