package com.gestion.funcionarios.service;

import com.gestion.funcionarios.dao.InventarioDAO;
import com.gestion.funcionarios.dao.impl.InventarioDAOImpl;
import com.gestion.funcionarios.exception.DAOException;
import com.gestion.funcionarios.exception.UnauthorizedException;
import com.gestion.funcionarios.model.Inventario;
import com.gestion.funcionarios.security.SessionContext;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de negocio para el módulo de Inventarios.
 *
 * Reglas de acceso (token requerido en todos los métodos):
 *   ADMINISTRADOR : CRUD completo.
 *   DOCENTE       : solo lectura (findAll, findByTexto, findById).
 */
public class InventarioService {

    private final InventarioDAO  inventarioDAO;
    private final SessionContext session;

    public InventarioService() {
        this.inventarioDAO = new InventarioDAOImpl();
        this.session       = SessionContext.getInstance();
    }

    public InventarioService(InventarioDAO inventarioDAO) {
        this.inventarioDAO = inventarioDAO;
        this.session       = SessionContext.getInstance();
    }

    // ── Lectura (ADMINISTRADOR y DOCENTE) ────────────────────────────────────

    public List<Inventario> findAll() throws DAOException {
        session.requireToken(); // cualquier rol puede leer
        return inventarioDAO.findAll();
    }

    public List<Inventario> findByTexto(String texto) throws DAOException {
        session.requireToken();
        return inventarioDAO.findByTexto(texto);
    }

    public Optional<Inventario> findById(int id) throws DAOException {
        session.requireToken();
        return inventarioDAO.findById(id);
    }

    // ── Escritura (solo ADMINISTRADOR) ──────────────────────────────────────

    public Inventario create(Inventario inventario) throws DAOException, UnauthorizedException {
        requireAdmin();
        return inventarioDAO.save(inventario);
    }

    public boolean update(Inventario inventario) throws DAOException, UnauthorizedException {
        requireAdmin();
        return inventarioDAO.update(inventario);
    }

    public boolean deleteById(int id) throws DAOException, UnauthorizedException {
        requireAdmin();
        return inventarioDAO.deleteById(id);
    }

    public boolean existsByCodigo(String codigo, int excludeId)
            throws DAOException, UnauthorizedException {
        requireAdmin();
        return inventarioDAO.existsByCodigo(codigo, excludeId);
    }

    // ── Helper ───────────────────────────────────────────────────────────────

    private void requireAdmin() throws UnauthorizedException {
        session.requireToken();
        if (!session.isAdmin()) {
            throw new UnauthorizedException("Gestión de Inventarios (escritura)");
        }
    }
}
