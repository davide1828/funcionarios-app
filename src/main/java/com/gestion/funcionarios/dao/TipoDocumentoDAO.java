package com.gestion.funcionarios.dao;

import com.gestion.funcionarios.exception.DAOException;
import com.gestion.funcionarios.model.TipoDocumento;

import java.util.List;

/**
 * DAO de solo lectura para el catálogo de tipos de documento.
 */
public interface TipoDocumentoDAO {

    /**
     * Recupera todos los tipos de documento activos.
     *
     * @return lista de tipos de documento (nunca null).
     * @throws DAOException ante errores de acceso a datos.
     */
    List<TipoDocumento> findAllActivos() throws DAOException;
}
