package com.gestion.funcionarios.dao.impl;

import com.gestion.funcionarios.config.DatabaseConnection;
import com.gestion.funcionarios.dao.EstadoEquipoDAO;
import com.gestion.funcionarios.exception.DAOException;
import com.gestion.funcionarios.model.EstadoEquipo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Implementación JDBC de {@link EstadoEquipoDAO}. */
public class EstadoEquipoDAOImpl implements EstadoEquipoDAO {

    private static final String TABLE = "estados_equipo";

    @Override
    public List<EstadoEquipo> findAll() throws DAOException {
        return query("SELECT id, nombre, descripcion, activo FROM " + TABLE + " ORDER BY nombre", false);
    }

    @Override
    public List<EstadoEquipo> findAllActivos() throws DAOException {
        return query("SELECT id, nombre, descripcion, activo FROM " + TABLE + " WHERE activo = TRUE ORDER BY nombre", false);
    }

    @Override
    public Optional<EstadoEquipo> findById(int id) throws DAOException {
        String sql = "SELECT id, nombre, descripcion, activo FROM " + TABLE + " WHERE id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error al buscar EstadoEquipo por id: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public EstadoEquipo save(EstadoEquipo entity) throws DAOException {
        String sql = "INSERT INTO " + TABLE + " (nombre, descripcion) VALUES (?, ?) RETURNING id";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, entity.getNombre());
            ps.setString(2, entity.getDescripcion());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) entity.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState()))
                throw new DAOException("Ya existe un registro con ese nombre.", 23505, e);
            throw new DAOException("Error al guardar EstadoEquipo: " + e.getMessage(), e);
        }
        return entity;
    }

    @Override
    public boolean update(EstadoEquipo entity) throws DAOException {
        String sql = "UPDATE " + TABLE + " SET nombre=?, descripcion=?, activo=? WHERE id=?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, entity.getNombre());
            ps.setString(2, entity.getDescripcion());
            ps.setBoolean(3, entity.isActivo());
            ps.setInt(4, entity.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error al actualizar EstadoEquipo: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteById(int id) throws DAOException {
        String sql = "DELETE FROM " + TABLE + " WHERE id=?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error al eliminar EstadoEquipo: " + e.getMessage(), e);
        }
    }

    private List<EstadoEquipo> query(String sql, boolean hasWhere) throws DAOException {
        List<EstadoEquipo> lista = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(map(rs));
        } catch (SQLException e) {
            throw new DAOException("Error al consultar EstadoEquipo: " + e.getMessage(), e);
        }
        return lista;
    }

    private EstadoEquipo map(ResultSet rs) throws SQLException {
        return new EstadoEquipo(rs.getInt("id"), rs.getString("nombre"),
                rs.getString("descripcion"), rs.getBoolean("activo"));
    }
}
