package com.gestion.funcionarios.security;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utilidad para cifrado y verificación de contraseñas con BCrypt.
 *
 * Nunca se almacena la contraseña en texto plano;
 * solo el hash generado por BCrypt (cost factor = 12).
 */
public final class PasswordUtil {

    private static final int BCRYPT_COST = 12;

    private PasswordUtil() {}

    /**
     * Genera el hash BCrypt de una contraseña en texto plano.
     *
     * @param plainPassword contraseña sin cifrar.
     * @return hash listo para persistir en base de datos.
     */
    public static String hash(String plainPassword) {
        if (plainPassword == null || plainPassword.isBlank()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía.");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(BCRYPT_COST));
    }

    /**
     * Verifica si una contraseña en texto plano coincide con su hash.
     *
     * @param plainPassword contraseña proporcionada por el usuario.
     * @param hashedPassword hash almacenado en base de datos.
     * @return {@code true} si la contraseña es correcta.
     */
    public static boolean verify(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) return false;
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (Exception e) {
            return false;
        }
    }
}
