package com.gestion.funcionarios.util;

import com.gestion.funcionarios.config.DatabaseConnection;
import com.gestion.funcionarios.exception.DAOException;
import java.sql.*;

/**
 * Utilitario para ejecutar scripts SQL de inicialización.
 */
public class DatabaseInitializer {

    public static void main(String[] args) {
        try {
            System.out.println("Actualizando contraseñas de funcionarios...");
            updatePasswords();
            System.out.println("✅ Actualización completada exitosamente");
        } catch (SQLException | DAOException e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void updatePasswords() throws SQLException, DAOException {
        // Hash BCrypt de 'Admin123*' con cost=12
        String passwordHash = "$2a$12$ZVhHfQ4YyznHjohYA14qNe8fHXpCvwJIIlT3A6gHe.Np9fBD2ldPC";

        try (Connection con = DatabaseConnection.getConnection()) {
            // 1. Actualizar todos los funcionarios sin contraseña
            System.out.println("1. Actualizando contraseñas para todos los funcionarios...");
            String sql1 = "UPDATE funcionarios SET password_hash = ?";
            try (PreparedStatement stmt = con.prepareStatement(sql1)) {
                stmt.setString(1, passwordHash);
                int updated = stmt.executeUpdate();
                System.out.println("   → " + updated + " funcionarios actualizados");
            }

            // 2. Asignar rol ADMINISTRADOR a Carlos Ramírez (Gerente General)
            System.out.println("2. Asignando rol ADMINISTRADOR a caramirez@entidad.gov.co...");
            String sql2 = "UPDATE funcionarios SET rol = 'ADMINISTRADOR' WHERE email = ?";
            try (PreparedStatement stmt = con.prepareStatement(sql2)) {
                stmt.setString(1, "caramirez@entidad.gov.co");
                int updated = stmt.executeUpdate();
                System.out.println("   → " + updated + " funcionarios actualizados");
            }

            // 3. Asignar rol ADMINISTRADOR a otros líderes
            System.out.println("3. Asignando rol ADMINISTRADOR a líderes...");
            String sql3 = "UPDATE funcionarios SET rol = 'ADMINISTRADOR' WHERE email IN (?, ?, ?)";
            try (PreparedStatement stmt = con.prepareStatement(sql3)) {
                stmt.setString(1, "lfgomez@entidad.gov.co");
                stmt.setString(2, "damorales@entidad.gov.co");
                stmt.setString(3, "afvargas@entidad.gov.co");
                int updated = stmt.executeUpdate();
                System.out.println("   → " + updated + " funcionarios actualizados");
            }

            // 4. Verificar
            System.out.println("\n4. Verificando datos...");
            String sqlVerify = "SELECT email, password_hash IS NOT NULL as tiene_contraseña, rol FROM funcionarios LIMIT 5";
            try (Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(sqlVerify)) {
                System.out.println("   Email                          | Contraseña | Rol");
                System.out.println("   " + "-".repeat(70));
                while (rs.next()) {
                    String email = rs.getString("email");
                    boolean tiene = rs.getBoolean("tiene_contraseña");
                    String rol = rs.getString("rol");
                    System.out.printf("   %-30s | %-10s | %s%n", email, tiene ? "✅" : "❌", rol);
                }
            }
        }
    }
}
