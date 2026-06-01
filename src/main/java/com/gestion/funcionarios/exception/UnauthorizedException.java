package com.gestion.funcionarios.exception;

/**
 * Excepción lanzada cuando el usuario autenticado no tiene
 * los permisos necesarios para ejecutar la operación solicitada.
 */
public class UnauthorizedException extends Exception {

    private static final long serialVersionUID = 1L;

    public UnauthorizedException(String operation) {
        super("Sin permisos para ejecutar: " + operation +
              ". Se requiere rol ADMINISTRADOR.");
    }
}
