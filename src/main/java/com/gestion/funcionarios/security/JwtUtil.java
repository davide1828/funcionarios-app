package com.gestion.funcionarios.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Utilidad para generación y validación de tokens JWT.
 *
 * El token incluye:
 *   - sub    : email del usuario
 *   - rol    : rol del usuario (ADMINISTRADOR | DOCENTE)
 *   - userId : id del funcionario
 *   - exp    : expiración (8 horas por defecto)
 */
public final class JwtUtil {

    // En producción esta clave debe venir de una variable de entorno / keystore
    private static final String SECRET_STRING =
        "gestion-funcionarios-secret-key-2025-muy-larga-y-segura";
    private static final SecretKey SECRET_KEY =
        Keys.hmacShaKeyFor(SECRET_STRING.getBytes(StandardCharsets.UTF_8));

    private static final long EXPIRY_MS = 8L * 60 * 60 * 1000; // 8 horas

    private static final String CLAIM_ROL     = "rol";
    private static final String CLAIM_USER_ID = "userId";

    private JwtUtil() {}

    /**
     * Genera un JWT firmado para el usuario autenticado.
     *
     * @param email  correo del funcionario (subject).
     * @param rol    rol asignado al funcionario.
     * @param userId id del funcionario en la BD.
     * @return token JWT compacto como String.
     */
    public static String generateToken(String email, Role rol, int userId) {
        Date now    = new Date();
        Date expiry = new Date(now.getTime() + EXPIRY_MS);

        return Jwts.builder()
                .subject(email)
                .claim(CLAIM_ROL, rol.name())
                .claim(CLAIM_USER_ID, userId)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(SECRET_KEY)
                .compact();
    }

    /**
     * Valida el token y retorna sus claims si es correcto.
     *
     * @param token JWT a validar.
     * @return claims del token.
     * @throws JwtException si el token es inválido, expirado o mal formado.
     */
    public static Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Extrae el email (subject) del token sin lanzar excepción
     * (útil para mostrar en UI).
     */
    public static String extractEmail(String token) {
        return parseToken(token).getSubject();
    }

    /**
     * Extrae el rol del token.
     */
    public static Role extractRole(String token) {
        String rolName = parseToken(token).get(CLAIM_ROL, String.class);
        return Role.fromString(rolName);
    }

    /**
     * Extrae el id del funcionario del token.
     */
    public static int extractUserId(String token) {
        return parseToken(token).get(CLAIM_USER_ID, Integer.class);
    }

    /**
     * Verifica si el token es válido (no expirado y firma correcta).
     */
    public static boolean isValid(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
