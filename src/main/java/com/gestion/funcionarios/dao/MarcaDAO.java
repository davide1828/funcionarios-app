package com.gestion.funcionarios.dao;

import com.gestion.funcionarios.exception.DAOException;
import com.gestion.funcionarios.model.Marca;
import java.util.List;
import java.util.Optional;

/** DAO para el catálogo {@link Marca}. */
public interface MarcaDAO {
    List<Marca>     findAll()                            throws DAOException;
    List<Marca>     findAllActivos()                     throws DAOException;
    Optional<Marca> findById(int id)                    throws DAOException;
    Marca           save(Marca entity)              throws DAOException;
    boolean             update(Marca entity)            throws DAOException;
    boolean             deleteById(int id)                  throws DAOException;
}
