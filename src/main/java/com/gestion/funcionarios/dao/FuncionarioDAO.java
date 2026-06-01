package com.gestion.funcionarios.dao;

import com.gestion.funcionarios.exception.DAOException;
import com.gestion.funcionarios.model.Funcionario;

import java.util.List;
import java.util.Optional;

/**
 * Contrato del patrón DAO para la entidad {@link Funcionario}.
 *
 * Define las cuatro operaciones CRUD más búsquedas auxiliares.
 * Las implementaciones concretas son las únicas que conocen SQL;
 * el resto de la aplicación trabaja contra esta interfaz.
 */
public interface FuncionarioDAO {

    /**
     * Recupera todos los funcionarios con sus relaciones.
     *
     * @return lista (puede estar vacía, nunca null).
     * @throws DAOException si ocurre un error de acceso a datos.
     */
    List<Funcionario> findAll() throws DAOException;

    /**
     * Busca un funcionario por su clave primaria.
     *
     * @param id identificador del funcionario.
     * @return {@link Optional} con el funcionario o vacío si no existe.
     * @throws DAOException si ocurre un error de acceso a datos.
     */
    Optional<Funcionario> findById(int id) throws DAOException;

    /**
     * Busca funcionarios cuyo nombre, apellido o número de documento
     * contenga el texto indicado (búsqueda flexible).
     *
     * @param texto fragmento a buscar (insensible a mayúsculas).
     * @return lista filtrada.
     * @throws DAOException si ocurre un error de acceso a datos.
     */
    List<Funcionario> findByTexto(String texto) throws DAOException;

    /**
     * Inserta un nuevo funcionario en la base de datos.
     *
     * @param funcionario objeto a persistir (id será asignado por la BD).
     * @return el mismo objeto con el id generado.
     * @throws DAOException si ocurre un error de acceso a datos.
     */
    Funcionario save(Funcionario funcionario) throws DAOException;

    /**
     * Actualiza los datos de un funcionario existente.
     *
     * @param funcionario objeto con los nuevos valores (debe tener id válido).
     * @return {@code true} si se actualizó al menos una fila.
     * @throws DAOException si ocurre un error de acceso a datos.
     */
    boolean update(Funcionario funcionario) throws DAOException;

    /**
     * Elimina un funcionario por su id.
     *
     * @param id clave primaria del funcionario a eliminar.
     * @return {@code true} si se eliminó al menos una fila.
     * @throws DAOException si ocurre un error de acceso a datos.
     */
    boolean deleteById(int id) throws DAOException;

    /**
     * Busca un funcionario por su email.
     *
     * @param email correo del funcionario.
     * @return {@link Optional} con el funcionario o vacío si no existe.
     * @throws DAOException si ocurre un error de acceso a datos.
     */
    Optional<Funcionario> findByEmail(String email) throws DAOException;

    /**
     * Verifica si ya existe un funcionario con el número de documento dado,
     * excluyendo opcionalmente un id (útil en actualizaciones).
     *
     * @param numeroDocumento número a verificar.
     * @param excludeId       id a excluir de la búsqueda (0 para no excluir).
     * @return {@code true} si el documento ya está en uso.
     * @throws DAOException si ocurre un error de acceso a datos.
     */
    boolean existsByNumeroDocumento(String numeroDocumento, int excludeId) throws DAOException;
}
