package com.gestion.funcionarios.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import com.gestion.funcionarios.security.Role;

/**
 * Entidad principal del sistema.
 * Representa a un funcionario de la entidad con todas sus relaciones.
 *
 * Relaciones:
 * - tipo_documento (N:1)
 * - cargo (N:1) → cargo (N:1) area
 * - municipio (N:1) → municipio (N:1) departamento
 */
public class Funcionario {

    private int id;
    private String nombres;
    private String apellidos;
    private TipoDocumento tipoDocumento;
    private String numeroDocumento;
    private LocalDate fechaNacimiento;
    private LocalDate fechaIngreso;
    private String email;
    private String telefono;
    private Cargo cargo;
    private Municipio municipio;
    private String estado; // ACTIVO | INACTIVO
    private String passwordHash; // BCrypt hash de la contraseña
    private Role rol; // Rol del funcionario (ADMINISTRADOR | DOCENTE)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Funcionario() {
    }

    // ── Getters / Setters ──────────────────────────────────────────────────
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String n) {
        this.nombres = n;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String a) {
        this.apellidos = a;
    }

    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocumento t) {
        this.tipoDocumento = t;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String nd) {
        this.numeroDocumento = nd;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate f) {
        this.fechaNacimiento = f;
    }

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(LocalDate f) {
        this.fechaIngreso = f;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String e) {
        this.email = e;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String t) {
        this.telefono = t;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public void setCargo(Cargo c) {
        this.cargo = c;
    }

    public Municipio getMunicipio() {
        return municipio;
    }

    public void setMunicipio(Municipio m) {
        this.municipio = m;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String e) {
        this.estado = e;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String ph) {
        this.passwordHash = ph;
    }

    public Role getRol() {
        return rol;
    }

    public void setRol(Role r) {
        this.rol = r;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime c) {
        this.createdAt = c;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime u) {
        this.updatedAt = u;
    }

    /** Nombre completo conveniente para mostrar en la UI. */
    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }

    @Override
    public String toString() {
        return "[" + id + "] " + getNombreCompleto() + " – " + numeroDocumento;
    }
}
