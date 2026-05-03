package com.gestion.funcionarios.dao;

import com.gestion.funcionarios.exception.DAOException;
import com.gestion.funcionarios.model.Cargo;

import java.util.List;

/**
 * DAO de solo lectura para los catálogos de cargos.
 * No expone operaciones de escritura porque su gestión queda fuera del
 * alcance de este módulo (CRUD de funcionarios).
 */
public interface CargoDAO {

    /**
     * Recupera todos los cargos activos con su área asociada.
     *
     * @return lista de cargos (nunca null).
     * @throws DAOException ante errores de acceso a datos.
     */
    List<Cargo> findAllActivos() throws DAOException;
}
