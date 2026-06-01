package com.gestion.funcionarios.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Equipo registrado en el inventario de la entidad.
 *
 * Relaciones:
 *   estado    (N:1) → EstadoEquipo
 *   marca     (N:1) → Marca
 *   tipo      (N:1) → TipoEquipo
 *   asignado  (N:1) → Funcionario (nullable)
 */
public class Inventario {

    private int          id;
    private String       codigo;
    private String       nombre;
    private String       descripcion;
    private EstadoEquipo estado;
    private Marca        marca;
    private TipoEquipo   tipo;
    private Funcionario  funcionarioAsignado;   // puede ser null
    private LocalDate    fechaRegistro;
    private boolean      activo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Inventario() {}

    // ── Getters / Setters ──────────────────────────────────────────────────
    public int          getId()                        { return id; }
    public void         setId(int id)                  { this.id = id; }

    public String       getCodigo()                    { return codigo; }
    public void         setCodigo(String c)            { this.codigo = c; }

    public String       getNombre()                    { return nombre; }
    public void         setNombre(String n)            { this.nombre = n; }

    public String       getDescripcion()               { return descripcion; }
    public void         setDescripcion(String d)       { this.descripcion = d; }

    public EstadoEquipo getEstado()                    { return estado; }
    public void         setEstado(EstadoEquipo e)      { this.estado = e; }

    public Marca        getMarca()                     { return marca; }
    public void         setMarca(Marca m)              { this.marca = m; }

    public TipoEquipo   getTipo()                      { return tipo; }
    public void         setTipo(TipoEquipo t)          { this.tipo = t; }

    public Funcionario  getFuncionarioAsignado()       { return funcionarioAsignado; }
    public void         setFuncionarioAsignado(Funcionario f) { this.funcionarioAsignado = f; }

    public LocalDate    getFechaRegistro()             { return fechaRegistro; }
    public void         setFechaRegistro(LocalDate f)  { this.fechaRegistro = f; }

    public boolean      isActivo()                     { return activo; }
    public void         setActivo(boolean a)           { this.activo = a; }

    public LocalDateTime getCreatedAt()                { return createdAt; }
    public void          setCreatedAt(LocalDateTime c) { this.createdAt = c; }

    public LocalDateTime getUpdatedAt()                { return updatedAt; }
    public void          setUpdatedAt(LocalDateTime u) { this.updatedAt = u; }

    @Override
    public String toString() { return "[" + codigo + "] " + nombre; }
}
