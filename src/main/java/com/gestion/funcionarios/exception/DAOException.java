package com.gestion.funcionarios.exception;

/**
 * Excepción checked que encapsula errores producidos en la capa de acceso
 * a datos (DAO). Permite propagar mensajes descriptivos hacia la capa de
 * presentación sin exponer detalles de SQL.
 */
public class DAOException extends Exception {

    private static final long serialVersionUID = 1L;

    /** Código de error opcional para clasificar el tipo de fallo. */
    private final int errorCode;

    public DAOException(String message) {
        super(message);
        this.errorCode = 0;
    }

    public DAOException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = 0;
    }

    public DAOException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public DAOException(String message, int errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
