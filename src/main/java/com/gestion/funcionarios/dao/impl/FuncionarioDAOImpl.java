package com.gestion.funcionarios.dao.impl;

import com.gestion.funcionarios.config.DatabaseConnection;
import com.gestion.funcionarios.dao.FuncionarioDAO;
import com.gestion.funcionarios.exception.DAOException;
import com.gestion.funcionarios.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación JDBC del {@link FuncionarioDAO}.
 *
 * Aplica el patrón DAO puro:
 * - Toda la lógica SQL queda aquí, aislada del resto de la aplicación.
 * - Usa PreparedStatement para prevenir inyección SQL.
 * - Gestiona los recursos con try-with-resources.
 * - Propaga errores únicamente como {@link DAOException}.
 */
public class FuncionarioDAOImpl implements FuncionarioDAO {

    // ── Sentencias SQL ──────────────────────────────────────────────────────

    /** Columnas comunes del SELECT con todos los JOINs necesarios. */
    private static final String BASE_SELECT = "SELECT f.id, f.nombres, f.apellidos, f.numero_documento, " +
            "       f.fecha_nacimiento, f.fecha_ingreso, f.email, f.telefono, " +
            "       f.estado, f.created_at, f.updated_at, " +
            // tipo_documento
            "       td.id AS td_id, td.codigo AS td_codigo, td.nombre AS td_nombre, td.activo AS td_activo, " +
            // cargo
            "       c.id AS c_id, c.nombre AS c_nombre, c.nivel_salarial, c.activo AS c_activo, " +
            // area (via cargo)
            "       a.id AS a_id, a.nombre AS a_nombre, a.descripcion AS a_desc, a.activo AS a_activo, " +
            // municipio
            "       m.id AS m_id, m.codigo AS m_codigo, m.nombre AS m_nombre, m.activo AS m_activo, " +
            // departamento (via municipio)
            "       d.id AS d_id, d.codigo AS d_codigo, d.nombre AS d_nombre, d.activo AS d_activo " +
            "FROM funcionarios f " +
            "INNER JOIN tipo_documento td ON td.id = f.tipo_doc_id " +
            "INNER JOIN cargos         c  ON c.id  = f.cargo_id " +
            "INNER JOIN areas          a  ON a.id  = c.area_id " +
            "INNER JOIN municipios     m  ON m.id  = f.municipio_id " +
            "INNER JOIN departamentos  d  ON d.id  = m.departamento_id ";

    private static final String SQL_FIND_ALL = BASE_SELECT + "ORDER BY f.apellidos, f.nombres";

    private static final String SQL_FIND_BY_ID = BASE_SELECT + "WHERE f.id = ?";

    private static final String SQL_FIND_BY_EMAIL = BASE_SELECT + "WHERE f.email = ?";

    private static final String SQL_FIND_BY_TEXTO = BASE_SELECT +
            "WHERE LOWER(f.nombres || ' ' || f.apellidos) LIKE LOWER(?) " +
            "   OR f.numero_documento LIKE ? " +
            "ORDER BY f.apellidos, f.nombres";

    private static final String SQL_INSERT = "INSERT INTO funcionarios " +
            "(nombres, apellidos, tipo_doc_id, numero_documento, fecha_nacimiento, " +
            " fecha_ingreso, email, telefono, cargo_id, municipio_id, estado) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
            "RETURNING id";

    private static final String SQL_UPDATE = "UPDATE funcionarios SET " +
            "  nombres = ?, apellidos = ?, tipo_doc_id = ?, numero_documento = ?, " +
            "  fecha_nacimiento = ?, fecha_ingreso = ?, email = ?, telefono = ?, " +
            "  cargo_id = ?, municipio_id = ?, estado = ? " +
            "WHERE id = ?";

    private static final String SQL_DELETE = "DELETE FROM funcionarios WHERE id = ?";

    private static final String SQL_EXISTS_DOC = "SELECT COUNT(*) FROM funcionarios " +
            "WHERE numero_documento = ? AND id <> ?";

    // ── CRUD ────────────────────────────────────────────────────────────────

    @Override
    public List<Funcionario> findAll() throws DAOException {
        List<Funcionario> lista = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(SQL_FIND_ALL);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next())
                lista.add(mapRow(rs));
        } catch (SQLException e) {
            throw new DAOException("Error al listar funcionarios: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public Optional<Funcionario> findById(int id) throws DAOException {
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(SQL_FIND_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error al buscar funcionario por id: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Funcionario> findByEmail(String email) throws DAOException {
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(SQL_FIND_BY_EMAIL)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error al buscar funcionario por email: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Funcionario> findByTexto(String texto) throws DAOException {
        List<Funcionario> lista = new ArrayList<>();
        String patron = "%" + texto + "%";
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(SQL_FIND_BY_TEXTO)) {
            ps.setString(1, patron);
            ps.setString(2, patron);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    lista.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error en búsqueda de funcionarios: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public Funcionario save(Funcionario f) throws DAOException {
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(SQL_INSERT)) {

            bindParams(ps, f);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    f.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            // Código 23505 = unique_violation en PostgreSQL
            if ("23505".equals(e.getSQLState())) {
                throw new DAOException(
                        "Ya existe un funcionario con ese número de documento o correo.", 23505, e);
            }
            throw new DAOException("Error al guardar funcionario: " + e.getMessage(), e);
        }
        return f;
    }

    @Override
    public boolean update(Funcionario f) throws DAOException {
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(SQL_UPDATE)) {

            bindParams(ps, f);
            ps.setInt(12, f.getId()); // WHERE id = ?
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                throw new DAOException(
                        "Ya existe un funcionario con ese número de documento o correo.", 23505, e);
            }
            throw new DAOException("Error al actualizar funcionario: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteById(int id) throws DAOException {
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(SQL_DELETE)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error al eliminar funcionario: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsByNumeroDocumento(String numeroDocumento, int excludeId) throws DAOException {
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(SQL_EXISTS_DOC)) {
            ps.setString(1, numeroDocumento);
            ps.setInt(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new DAOException("Error al verificar documento: " + e.getMessage(), e);
        }
    }

    // ── Métodos auxiliares privados ─────────────────────────────────────────

    /**
     * Vincula los parámetros de un funcionario a un PreparedStatement.
     * Se usa tanto para INSERT como para UPDATE (los 11 campos de datos).
     */
    private void bindParams(PreparedStatement ps, Funcionario f) throws SQLException {
        ps.setString(1, f.getNombres());
        ps.setString(2, f.getApellidos());
        ps.setInt(3, f.getTipoDocumento().getId());
        ps.setString(4, f.getNumeroDocumento());
        ps.setDate(5, Date.valueOf(f.getFechaNacimiento()));
        ps.setDate(6, Date.valueOf(f.getFechaIngreso()));
        ps.setString(7, f.getEmail());
        ps.setString(8, f.getTelefono());
        ps.setInt(9, f.getCargo().getId());
        ps.setInt(10, f.getMunicipio().getId());
        ps.setString(11, f.getEstado());
    }

    /**
     * Construye un objeto {@link Funcionario} completo desde una fila del
     * ResultSet.
     * Hidrata todas las entidades relacionadas.
     */
    private Funcionario mapRow(ResultSet rs) throws SQLException {
        // Tipo de documento
        TipoDocumento tipoDoc = new TipoDocumento(
                rs.getInt("td_id"), rs.getString("td_codigo"),
                rs.getString("td_nombre"), rs.getBoolean("td_activo"));

        // Área
        Area area = new Area(
                rs.getInt("a_id"), rs.getString("a_nombre"),
                rs.getString("a_desc"), rs.getBoolean("a_activo"));

        // Cargo
        Cargo cargo = new Cargo(
                rs.getInt("c_id"), rs.getString("c_nombre"),
                rs.getString("nivel_salarial"), area, rs.getBoolean("c_activo"));

        // Departamento
        Departamento depto = new Departamento(
                rs.getInt("d_id"), rs.getString("d_codigo"),
                rs.getString("d_nombre"), rs.getBoolean("d_activo"));

        // Municipio
        Municipio municipio = new Municipio(
                rs.getInt("m_id"), rs.getString("m_codigo"),
                rs.getString("m_nombre"), depto, rs.getBoolean("m_activo"));

        // Funcionario principal
        Funcionario f = new Funcionario();
        f.setId(rs.getInt("id"));
        f.setNombres(rs.getString("nombres"));
        f.setApellidos(rs.getString("apellidos"));
        f.setTipoDocumento(tipoDoc);
        f.setNumeroDocumento(rs.getString("numero_documento"));
        f.setFechaNacimiento(rs.getDate("fecha_nacimiento").toLocalDate());
        f.setFechaIngreso(rs.getDate("fecha_ingreso").toLocalDate());
        f.setEmail(rs.getString("email"));
        f.setTelefono(rs.getString("telefono"));
        f.setCargo(cargo);
        f.setMunicipio(municipio);
        f.setEstado(rs.getString("estado"));
        f.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        f.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return f;
    }
}
