package com.gestion.funcionarios.dao;

import com.gestion.funcionarios.exception.DAOException;
import com.gestion.funcionarios.model.TipoEquipo;
import java.util.List;
import java.util.Optional;

/** DAO para el catálogo {@link TipoEquipo}. */
public interface TipoEquipoDAO {
    List<TipoEquipo>     findAll()                            throws DAOException;
    List<TipoEquipo>     findAllActivos()                     throws DAOException;
    Optional<TipoEquipo> findById(int id)                    throws DAOException;
    TipoEquipo           save(TipoEquipo entity)              throws DAOException;
    boolean             update(TipoEquipo entity)            throws DAOException;
    boolean             deleteById(int id)                  throws DAOException;
}
