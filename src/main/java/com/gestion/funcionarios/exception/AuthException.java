package com.gestion.funcionarios.exception;

/**
 * Excepción lanzada cuando las credenciales de autenticación
 * son incorrectas o el usuario no existe.
 */
public class AuthException extends Exception {

    private static final long serialVersionUID = 1L;

    public AuthException(String message) {
        super(message);
    }

    public AuthException(String message, Throwable cause) {
        super(message, cause);
    }
}
