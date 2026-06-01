package com.gestion.funcionarios.util;

import com.gestion.funcionarios.config.DatabaseConnection;
import com.gestion.funcionarios.exception.DAOException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;

/**
 * Ejecuta todos los scripts SQL de inicialización de la base de datos.
 */
public class DatabaseSetup {

    private static final String[] SCRIPTS = {
            "sql/01_schema.sql",
            "sql/02_data.sql",
            "sql/03_security_roles.sql",
            "sql/04_inventarios_schema.sql",
            "sql/05_inventarios_data.sql"
    };

    public static void main(String[] args) {
        try {
            System.out.println("═══════════════════════════════════════════════════════════");
            System.out.println("  INICIALIZANDO BASE DE DATOS - GESTIÓN DE FUNCIONARIOS");
            System.out.println("═══════════════════════════════════════════════════════════\n");

            for (String script : SCRIPTS) {
                executeScript(script);
            }

            System.out.println("\n✅ Base de datos inicializada exitosamente");
            System.out.println("═══════════════════════════════════════════════════════════\n");
            System.out.println("Credenciales de prueba:");
            System.out.println("  Email: caramirez@entidad.gov.co");
            System.out.println("  Contraseña: Admin123*");
            System.out.println("  Rol: ADMINISTRADOR\n");

        } catch (Exception e) {
            System.err.println("❌ Error durante la inicialización: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void executeScript(String scriptPath) throws IOException, DAOException, SQLException {
        System.out.println("📝 Ejecutando: " + scriptPath);
        String content = readScriptFile(scriptPath);

        try (Connection con = DatabaseConnection.getConnection()) {
            int count = 0;

            // Procesar líneas para manejar correctamente los bloques
            String[] lines = content.split("\n");
            StringBuilder currentStatement = new StringBuilder();
            boolean inDollarQuote = false;

            for (String line : lines) {
                String trimmedLine = line.trim();

                // Ignorar comentarios
                if (trimmedLine.startsWith("--")) {
                    continue;
                }

                // Detectar inicio/fin de bloques DO $$ ... $$
                if (trimmedLine.contains("$$")) {
                    inDollarQuote = !inDollarQuote;
                }

                currentStatement.append(line).append("\n");

                // Si no estamos en un bloque y encontramos punto y coma, ejecutar
                if (!inDollarQuote && trimmedLine.endsWith(";")) {
                    String sql = currentStatement.toString().trim();
                    if (!sql.isEmpty()) {
                        try {
                            executeStatement(con, sql);
                            count++;
                        } catch (SQLException e) {
                            if (!e.getMessage().contains("duplicate") &&
                                    !e.getMessage().contains("already exists") &&
                                    !e.getMessage().contains("does not exist")) {
                                System.out.println("   ⚠ Advertencia: " + e.getMessage());
                            }
                        }
                    }
                    currentStatement = new StringBuilder();
                }
            }

            // Ejecutar última sentencia si existe
            if (currentStatement.length() > 0) {
                String sql = currentStatement.toString().trim();
                if (!sql.isEmpty() && sql.endsWith(";")) {
                    try {
                        executeStatement(con, sql);
                        count++;
                    } catch (SQLException e) {
                        if (!e.getMessage().contains("duplicate")) {
                            System.out.println("   ⚠ Advertencia: " + e.getMessage());
                        }
                    }
                }
            }

            System.out.println("   ✅ " + count + " sentencias ejecutadas\n");
        }
    }

    private static void executeStatement(Connection con, String sql) throws SQLException {
        try (Statement stmt = con.createStatement()) {
            stmt.execute(sql);
        }
    }

    private static String readScriptFile(String scriptPath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(scriptPath)));
    }
}
