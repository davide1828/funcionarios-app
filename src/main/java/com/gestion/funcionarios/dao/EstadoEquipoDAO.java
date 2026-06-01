package com.gestion.funcionarios.dao;

import com.gestion.funcionarios.exception.DAOException;
import com.gestion.funcionarios.model.EstadoEquipo;
import java.util.List;
import java.util.Optional;

/** DAO para el catálogo {@link EstadoEquipo}. */
public interface EstadoEquipoDAO {
    List<EstadoEquipo>     findAll()                            throws DAOException;
    List<EstadoEquipo>     findAllActivos()                     throws DAOException;
    Optional<EstadoEquipo> findById(int id)                    throws DAOException;
    EstadoEquipo           save(EstadoEquipo entity)              throws DAOException;
    boolean             update(EstadoEquipo entity)            throws DAOException;
    boolean             deleteById(int id)                  throws DAOException;
}
