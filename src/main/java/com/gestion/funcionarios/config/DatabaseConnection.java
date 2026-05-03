package com.gestion.funcionarios.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.gestion.funcionarios.exception.DAOException;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Singleton que gestiona el pool de conexiones a PostgreSQL mediante HikariCP.
 * Centraliza la configuración de acceso a la base de datos.
 */
public final class DatabaseConnection {

    // ── Parámetros de conexión ──────────────────────────────────────────────
    private static final String URL = "jdbc:postgresql://localhost:5432/gestion_funcionarios";
    private static final String USER = "postgres";
    private static final String PASSWORD = "Admin"; // ← ajustar al entorno

    private static HikariDataSource dataSource;

    /** Constructor privado: clase utilitaria, no instanciable. */
    private DatabaseConnection() {
    }

    /**
     * Inicializa el pool HikariCP una sola vez (patrón Singleton con lazy init).
     */
    private static synchronized void initPool() {
        if (dataSource != null && !dataSource.isClosed())
            return;

        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(URL);
        cfg.setUsername(USER);
        cfg.setPassword(PASSWORD);
        cfg.setDriverClassName("org.postgresql.Driver");

        // Pool sizing
        cfg.setMaximumPoolSize(10);
        cfg.setMinimumIdle(2);
        cfg.setConnectionTimeout(30_000); // 30 s
        cfg.setIdleTimeout(600_000); // 10 min
        cfg.setMaxLifetime(1_800_000); // 30 min

        // Validación de conexiones vivas
        cfg.setConnectionTestQuery("SELECT 1");
        cfg.setPoolName("FuncionariosPool");

        dataSource = new HikariDataSource(cfg);
    }

    /**
     * Entrega una conexión activa del pool.
     *
     * @return {@link Connection} lista para usar.
     * @throws DAOException si no se puede obtener la conexión.
     */
    public static Connection getConnection() throws DAOException {
        try {
            initPool();
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new DAOException(
                    "No se pudo obtener conexión con la base de datos: " + e.getMessage(), e);
        }
    }

    /**
     * Cierra el pool de conexiones al apagar la aplicación.
     */
    public static synchronized void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
