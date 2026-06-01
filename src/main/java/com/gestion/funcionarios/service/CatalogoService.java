package com.gestion.funcionarios.service;

import com.gestion.funcionarios.dao.*;
import com.gestion.funcionarios.dao.impl.*;
import com.gestion.funcionarios.exception.DAOException;
import com.gestion.funcionarios.exception.UnauthorizedException;
import com.gestion.funcionarios.model.*;
import com.gestion.funcionarios.security.SessionContext;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de negocio para los catálogos del módulo de inventarios:
 * EstadoEquipo, Marca y TipoEquipo.
 *
 * Reglas de acceso (token requerido en todos los métodos):
 *   ADMINISTRADOR : CRUD completo.
 *   DOCENTE       : sin acceso (solo puede leer inventarios).
 */
public class CatalogoService {

    private final EstadoEquipoDAO estadoEquipoDAO;
    private final MarcaDAO        marcaDAO;
    private final TipoEquipoDAO   tipoEquipoDAO;
    private final SessionContext  session;

    public CatalogoService() {
        this.estadoEquipoDAO = new EstadoEquipoDAOImpl();
        this.marcaDAO        = new MarcaDAOImpl();
        this.tipoEquipoDAO   = new TipoEquipoDAOImpl();
        this.session         = SessionContext.getInstance();
    }

    // ── Estados de equipo ─────────────────────────────────────────────────

    public List<EstadoEquipo> findAllEstados() throws DAOException, UnauthorizedException {
        requireAdmin();
        return estadoEquipoDAO.findAll();
    }

    public List<EstadoEquipo> findEstadosActivos() throws DAOException, UnauthorizedException {
        // Solo ADMINISTRADOR: los docentes solo pueden visualizar inventarios
        requireAdmin();
        return estadoEquipoDAO.findAllActivos();
    }

    public EstadoEquipo createEstado(EstadoEquipo estado) throws DAOException, UnauthorizedException {
        requireAdmin();
        return estadoEquipoDAO.save(estado);
    }

    public boolean updateEstado(EstadoEquipo estado) throws DAOException, UnauthorizedException {
        requireAdmin();
        return estadoEquipoDAO.update(estado);
    }

    public boolean deleteEstadoById(int id) throws DAOException, UnauthorizedException {
        requireAdmin();
        return estadoEquipoDAO.deleteById(id);
    }

    // ── Marcas ────────────────────────────────────────────────────────────

    public List<Marca> findAllMarcas() throws DAOException, UnauthorizedException {
        requireAdmin();
        return marcaDAO.findAll();
    }

    public List<Marca> findMarcasActivas() throws DAOException, UnauthorizedException {
        requireAdmin();
        return marcaDAO.findAllActivos();
    }

    public Marca createMarca(Marca marca) throws DAOException, UnauthorizedException {
        requireAdmin();
        return marcaDAO.save(marca);
    }

    public boolean updateMarca(Marca marca) throws DAOException, UnauthorizedException {
        requireAdmin();
        return marcaDAO.update(marca);
    }

    public boolean deleteMarcaById(int id) throws DAOException, UnauthorizedException {
        requireAdmin();
        return marcaDAO.deleteById(id);
    }

    // ── Tipos de equipo ───────────────────────────────────────────────────

    public List<TipoEquipo> findAllTipos() throws DAOException, UnauthorizedException {
        requireAdmin();
        return tipoEquipoDAO.findAll();
    }

    public List<TipoEquipo> findTiposActivos() throws DAOException, UnauthorizedException {
        requireAdmin();
        return tipoEquipoDAO.findAllActivos();
    }

    public TipoEquipo createTipo(TipoEquipo tipo) throws DAOException, UnauthorizedException {
        requireAdmin();
        return tipoEquipoDAO.save(tipo);
    }

    public boolean updateTipo(TipoEquipo tipo) throws DAOException, UnauthorizedException {
        requireAdmin();
        return tipoEquipoDAO.update(tipo);
    }

    public boolean deleteTipoById(int id) throws DAOException, UnauthorizedException {
        requireAdmin();
        return tipoEquipoDAO.deleteById(id);
    }

    // ── Helper ────────────────────────────────────────────────────────────

    private void requireAdmin() throws UnauthorizedException {
        session.requireToken();
        if (!session.isAdmin()) {
            throw new UnauthorizedException("Gestión de Catálogos");
        }
    }
}
