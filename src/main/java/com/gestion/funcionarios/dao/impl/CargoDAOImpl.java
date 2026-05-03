package com.gestion.funcionarios.dao.impl;

import com.gestion.funcionarios.config.DatabaseConnection;
import com.gestion.funcionarios.dao.CargoDAO;
import com.gestion.funcionarios.exception.DAOException;
import com.gestion.funcionarios.model.Area;
import com.gestion.funcionarios.model.Cargo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación JDBC del {@link CargoDAO}.
 * Realiza JOIN con la tabla areas para hidratar el objeto completo.
 */
public class CargoDAOImpl implements CargoDAO {

    private static final String SQL_FIND_ALL_ACTIVOS =
        "SELECT c.id, c.nombre, c.nivel_salarial, c.activo, " +
        "       a.id AS area_id, a.nombre AS area_nombre, " +
        "       a.descripcion AS area_descripcion, a.activo AS area_activo " +
        "FROM cargos c " +
        "INNER JOIN areas a ON a.id = c.area_id " +
        "WHERE c.activo = TRUE " +
        "ORDER BY a.nombre, c.nombre";

    @Override
    public List<Cargo> findAllActivos() throws DAOException {
        List<Cargo> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_FIND_ALL_ACTIVOS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DAOException(
                "Error al consultar cargos: " + e.getMessage(), e);
        }
        return lista;
    }

    // ── Mapeo ResultSet → objeto ──────────────────────────────────────────
    private Cargo mapRow(ResultSet rs) throws SQLException {
        Area area = new Area(
            rs.getInt("area_id"),
            rs.getString("area_nombre"),
            rs.getString("area_descripcion"),
            rs.getBoolean("area_activo")
        );
        return new Cargo(
            rs.getInt("id"),
            rs.getString("nombre"),
            rs.getString("nivel_salarial"),
            area,
            rs.getBoolean("activo")
        );
    }
}
