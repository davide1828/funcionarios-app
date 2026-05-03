package com.gestion.funcionarios.dao;

import com.gestion.funcionarios.exception.DAOException;
import com.gestion.funcionarios.model.Municipio;

import java.util.List;

/**
 * DAO de solo lectura para el catálogo de municipios.
 */
public interface MunicipioDAO {

    /**
     * Recupera todos los municipios activos con su departamento.
     *
     * @return lista de municipios (nunca null).
     * @throws DAOException ante errores de acceso a datos.
     */
    List<Municipio> findAllActivos() throws DAOException;
}
