package com.gestion.funcionarios.dao.impl;

import com.gestion.funcionarios.config.DatabaseConnection;
import com.gestion.funcionarios.dao.InventarioDAO;
import com.gestion.funcionarios.exception.DAOException;
import com.gestion.funcionarios.model.*;
import com.gestion.funcionarios.security.Role;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Implementación JDBC del {@link InventarioDAO}. */
public class InventarioDAOImpl implements InventarioDAO {

    private static final String BASE_SELECT =
        "SELECT i.id, i.codigo, i.nombre, i.descripcion, i.fecha_registro, i.activo, " +
        "       i.created_at, i.updated_at, " +
        "       e.id AS e_id, e.nombre AS e_nombre, e.descripcion AS e_desc, e.activo AS e_activo, " +
        "       m.id AS m_id, m.nombre AS m_nombre, m.descripcion AS m_desc, m.activo AS m_activo, " +
        "       t.id AS t_id, t.nombre AS t_nombre, t.descripcion AS t_desc, t.activo AS t_activo, " +
        "       f.id AS f_id, f.nombres AS f_nombres, f.apellidos AS f_apellidos " +
        "FROM inventarios i " +
        "INNER JOIN estados_equipo e ON e.id = i.estado_id " +
        "INNER JOIN marcas         m ON m.id = i.marca_id " +
        "INNER JOIN tipos_equipo   t ON t.id = i.tipo_id " +
        "LEFT  JOIN funcionarios   f ON f.id = i.funcionario_id ";

    private static final String SQL_FIND_ALL =
        BASE_SELECT + "WHERE i.activo = TRUE ORDER BY i.codigo";

    private static final String SQL_FIND_BY_ID =
        BASE_SELECT + "WHERE i.id = ?";

    private static final String SQL_FIND_BY_TEXTO =
        BASE_SELECT +
        "WHERE i.activo = TRUE AND (" +
        "  LOWER(i.codigo) LIKE LOWER(?) OR LOWER(i.nombre) LIKE LOWER(?)) " +
        "ORDER BY i.codigo";

    private static final String SQL_INSERT =
        "INSERT INTO inventarios " +
        "(codigo, nombre, descripcion, estado_id, marca_id, tipo_id, funcionario_id, fecha_registro) " +
        "VALUES (?,?,?,?,?,?,?,?) RETURNING id";

    private static final String SQL_UPDATE =
        "UPDATE inventarios SET " +
        "  codigo=?, nombre=?, descripcion=?, estado_id=?, marca_id=?, tipo_id=?, " +
        "  funcionario_id=?, fecha_registro=? WHERE id=?";

    private static final String SQL_DELETE =
        "DELETE FROM inventarios WHERE id=?";

    private static final String SQL_EXISTS_CODIGO =
        "SELECT COUNT(*) FROM inventarios WHERE codigo=? AND id<>?";

    @Override
    public List<Inventario> findAll() throws DAOException {
        List<Inventario> lista = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapRow(rs));
        } catch (SQLException e) {
            throw new DAOException("Error al listar inventarios: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public List<Inventario> findByTexto(String texto) throws DAOException {
        String patron = "%" + texto + "%";
        List<Inventario> lista = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_FIND_BY_TEXTO)) {
            ps.setString(1, patron);
            ps.setString(2, patron);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error en búsqueda de inventario: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public Optional<Inventario> findById(int id) throws DAOException {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_FIND_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error al buscar inventario id=" + id + ": " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public Inventario save(Inventario inv) throws DAOException {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_INSERT)) {
            bindParams(ps, inv);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) inv.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState()))
                throw new DAOException("El código de inventario ya existe.", 23505, e);
            throw new DAOException("Error al guardar inventario: " + e.getMessage(), e);
        }
        return inv;
    }

    @Override
    public boolean update(Inventario inv) throws DAOException {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_UPDATE)) {
            bindParams(ps, inv);
            ps.setInt(9, inv.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState()))
                throw new DAOException("El código de inventario ya existe.", 23505, e);
            throw new DAOException("Error al actualizar inventario: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteById(int id) throws DAOException {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_DELETE)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error al eliminar inventario: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsByCodigo(String codigo, int excludeId) throws DAOException {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_EXISTS_CODIGO)) {
            ps.setString(1, codigo);
            ps.setInt(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new DAOException("Error al verificar código: " + e.getMessage(), e);
        }
    }

    private void bindParams(PreparedStatement ps, Inventario inv) throws SQLException {
        ps.setString(1, inv.getCodigo());
        ps.setString(2, inv.getNombre());
        ps.setString(3, inv.getDescripcion());
        ps.setInt(4,    inv.getEstado().getId());
        ps.setInt(5,    inv.getMarca().getId());
        ps.setInt(6,    inv.getTipo().getId());
        if (inv.getFuncionarioAsignado() != null) {
            ps.setInt(7, inv.getFuncionarioAsignado().getId());
        } else {
            ps.setNull(7, Types.INTEGER);
        }
        ps.setDate(8, Date.valueOf(inv.getFechaRegistro()));
    }

    private Inventario mapRow(ResultSet rs) throws SQLException {
        EstadoEquipo estado = new EstadoEquipo(rs.getInt("e_id"), rs.getString("e_nombre"),
            rs.getString("e_desc"), rs.getBoolean("e_activo"));
        Marca marca = new Marca(rs.getInt("m_id"), rs.getString("m_nombre"),
            rs.getString("m_desc"), rs.getBoolean("m_activo"));
        TipoEquipo tipo = new TipoEquipo(rs.getInt("t_id"), rs.getString("t_nombre"),
            rs.getString("t_desc"), rs.getBoolean("t_activo"));

        Funcionario asignado = null;
        int fId = rs.getInt("f_id");
        if (!rs.wasNull()) {
            asignado = new Funcionario();
            asignado.setId(fId);
            asignado.setNombres(rs.getString("f_nombres"));
            asignado.setApellidos(rs.getString("f_apellidos"));
        }

        Inventario inv = new Inventario();
        inv.setId(rs.getInt("id"));
        inv.setCodigo(rs.getString("codigo"));
        inv.setNombre(rs.getString("nombre"));
        inv.setDescripcion(rs.getString("descripcion"));
        inv.setEstado(estado);
        inv.setMarca(marca);
        inv.setTipo(tipo);
        inv.setFuncionarioAsignado(asignado);
        inv.setFechaRegistro(rs.getDate("fecha_registro").toLocalDate());
        inv.setActivo(rs.getBoolean("activo"));
        inv.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        inv.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return inv;
    }
}
