package com.gestion.funcionarios.dao.impl;

import com.gestion.funcionarios.config.DatabaseConnection;
import com.gestion.funcionarios.dao.MarcaDAO;
import com.gestion.funcionarios.exception.DAOException;
import com.gestion.funcionarios.model.Marca;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Implementación JDBC de {@link MarcaDAO}. */
public class MarcaDAOImpl implements MarcaDAO {

    private static final String TABLE = "marcas";

    @Override
    public List<Marca> findAll() throws DAOException {
        return query("SELECT id, nombre, descripcion, activo FROM " + TABLE + " ORDER BY nombre", false);
    }

    @Override
    public List<Marca> findAllActivos() throws DAOException {
        return query("SELECT id, nombre, descripcion, activo FROM " + TABLE + " WHERE activo = TRUE ORDER BY nombre", false);
    }

    @Override
    public Optional<Marca> findById(int id) throws DAOException {
        String sql = "SELECT id, nombre, descripcion, activo FROM " + TABLE + " WHERE id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error al buscar Marca por id: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public Marca save(Marca entity) throws DAOException {
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
            throw new DAOException("Error al guardar Marca: " + e.getMessage(), e);
        }
        return entity;
    }

    @Override
    public boolean update(Marca entity) throws DAOException {
        String sql = "UPDATE " + TABLE + " SET nombre=?, descripcion=?, activo=? WHERE id=?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, entity.getNombre());
            ps.setString(2, entity.getDescripcion());
            ps.setBoolean(3, entity.isActivo());
            ps.setInt(4, entity.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error al actualizar Marca: " + e.getMessage(), e);
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
            throw new DAOException("Error al eliminar Marca: " + e.getMessage(), e);
        }
    }

    private List<Marca> query(String sql, boolean hasWhere) throws DAOException {
        List<Marca> lista = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(map(rs));
        } catch (SQLException e) {
            throw new DAOException("Error al consultar Marca: " + e.getMessage(), e);
        }
        return lista;
    }

    private Marca map(ResultSet rs) throws SQLException {
        return new Marca(rs.getInt("id"), rs.getString("nombre"),
                rs.getString("descripcion"), rs.getBoolean("activo"));
    }
}
