package com.gestion.funcionarios.exception;

/**
 * Excepción checked para errores de validación de datos en la capa de
 * negocio / presentación antes de persistir en base de datos.
 */
public class ValidationException extends Exception {

    private static final long serialVersionUID = 1L;

    /** Campo o campo(s) que generaron el error de validación. */
    private final String field;

    public ValidationException(String message) {
        super(message);
        this.field = "N/A";
    }

    public ValidationException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
