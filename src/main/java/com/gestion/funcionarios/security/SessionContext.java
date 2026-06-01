package com.gestion.funcionarios.security;

/**
 * Contexto de sesión de la aplicación de escritorio.
 *
 * Actúa como portador del token JWT activo (equivalente al header
 * "Authorization: Bearer {token}" en una petición HTTP).
 *
 * Es un Singleton de hilo único porque Swing corre en el EDT.
 * Los servicios llaman a {@link #requireToken()} para obtener
 * el token vigente antes de ejecutar cualquier operación.
 */
public final class SessionContext {

    private static final SessionContext INSTANCE = new SessionContext();

    private String token;    // JWT activo
    private String email;
    private Role   role;
    private int    userId;

    private SessionContext() {}

    public static SessionContext getInstance() {
        return INSTANCE;
    }

    /**
     * Abre la sesión almacenando el token recién generado.
     */
    public void open(String token) {
        this.token  = token;
        this.email  = JwtUtil.extractEmail(token);
        this.role   = JwtUtil.extractRole(token);
        this.userId = JwtUtil.extractUserId(token);
    }

    /**
     * Cierra la sesión borrando todos los datos.
     */
    public void close() {
        this.token  = null;
        this.email  = null;
        this.role   = null;
        this.userId = 0;
    }

    /**
     * Retorna el token activo.
     *
     * @throws IllegalStateException si no hay sesión activa.
     */
    public String requireToken() {
        if (token == null || !JwtUtil.isValid(token)) {
            throw new IllegalStateException("Sesión no activa o token expirado.");
        }
        return token;
    }

    public boolean isActive()                { return token != null && JwtUtil.isValid(token); }
    public boolean hasRole(Role required)    { return role == required; }
    public boolean isAdmin()                 { return role == Role.ADMINISTRADOR; }

    public String getEmail()   { return email; }
    public Role   getRole()    { return role; }
    public int    getUserId()  { return userId; }
}
