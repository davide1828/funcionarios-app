package com.gestion.funcionarios.dao.impl;

import com.gestion.funcionarios.config.DatabaseConnection;
import com.gestion.funcionarios.dao.MunicipioDAO;
import com.gestion.funcionarios.exception.DAOException;
import com.gestion.funcionarios.model.Departamento;
import com.gestion.funcionarios.model.Municipio;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación JDBC del {@link MunicipioDAO}.
 * Realiza JOIN con la tabla departamentos para hidratar el objeto completo.
 */
public class MunicipioDAOImpl implements MunicipioDAO {

    private static final String SQL_FIND_ALL_ACTIVOS =
        "SELECT m.id, m.codigo, m.nombre, m.activo, " +
        "       d.id AS dep_id, d.codigo AS dep_codigo, " +
        "       d.nombre AS dep_nombre, d.activo AS dep_activo " +
        "FROM municipios m " +
        "INNER JOIN departamentos d ON d.id = m.departamento_id " +
        "WHERE m.activo = TRUE " +
        "ORDER BY d.nombre, m.nombre";

    @Override
    public List<Municipio> findAllActivos() throws DAOException {
        List<Municipio> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_FIND_ALL_ACTIVOS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DAOException(
                "Error al consultar municipios: " + e.getMessage(), e);
        }
        return lista;
    }

    // ── Mapeo ResultSet → objeto ──────────────────────────────────────────
    private Municipio mapRow(ResultSet rs) throws SQLException {
        Departamento dep = new Departamento(
            rs.getInt("dep_id"),
            rs.getString("dep_codigo"),
            rs.getString("dep_nombre"),
            rs.getBoolean("dep_activo")
        );
        return new Municipio(
            rs.getInt("id"),
            rs.getString("codigo"),
            rs.getString("nombre"),
            dep,
            rs.getBoolean("activo")
        );
    }
}
