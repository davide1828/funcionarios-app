package com.gestion.funcionarios.dao;

import com.gestion.funcionarios.exception.DAOException;
import com.gestion.funcionarios.model.Inventario;
import java.util.List;
import java.util.Optional;

/**
 * Contrato DAO para la entidad {@link Inventario}.
 */
public interface InventarioDAO {
    List<Inventario>     findAll()                     throws DAOException;
    List<Inventario>     findByTexto(String texto)     throws DAOException;
    Optional<Inventario> findById(int id)              throws DAOException;
    Inventario           save(Inventario inventario)   throws DAOException;
    boolean              update(Inventario inventario) throws DAOException;
    boolean              deleteById(int id)            throws DAOException;
    boolean              existsByCodigo(String codigo, int excludeId) throws DAOException;
}
