package com.gestion.funcionarios.dao.impl;

import com.gestion.funcionarios.config.DatabaseConnection;
import com.gestion.funcionarios.dao.TipoDocumentoDAO;
import com.gestion.funcionarios.exception.DAOException;
import com.gestion.funcionarios.model.TipoDocumento;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación JDBC del {@link TipoDocumentoDAO}.
 */
public class TipoDocumentoDAOImpl implements TipoDocumentoDAO {

    private static final String SQL_FIND_ALL_ACTIVOS =
        "SELECT id, codigo, nombre, activo " +
        "FROM tipo_documento " +
        "WHERE activo = TRUE " +
        "ORDER BY nombre";

    @Override
    public List<TipoDocumento> findAllActivos() throws DAOException {
        List<TipoDocumento> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_FIND_ALL_ACTIVOS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DAOException(
                "Error al consultar tipos de documento: " + e.getMessage(), e);
        }
        return lista;
    }

    // ── Mapeo ResultSet → objeto ──────────────────────────────────────────
    private TipoDocumento mapRow(ResultSet rs) throws SQLException {
        return new TipoDocumento(
            rs.getInt("id"),
            rs.getString("codigo"),
            rs.getString("nombre"),
            rs.getBoolean("activo")
        );
    }
}
