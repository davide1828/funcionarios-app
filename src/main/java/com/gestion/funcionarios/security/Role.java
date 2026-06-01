package com.gestion.funcionarios.security;

/**
 * Roles permitidos en el sistema.
 * ADMINISTRADOR : acceso total (CRUD en todos los módulos).
 * DOCENTE       : solo lectura en inventarios.
 */
public enum Role {
    ADMINISTRADOR,
    DOCENTE;

    /** Convierte el String de la BD al enum de forma segura. */
    public static Role fromString(String value) {
        if (value == null) return DOCENTE;
        return switch (value.toUpperCase().trim()) {
            case "ADMINISTRADOR" -> ADMINISTRADOR;
            default              -> DOCENTE;
        };
    }
}
