package com.gestion.funcionarios.service;

import com.gestion.funcionarios.dao.FuncionarioDAO;
import com.gestion.funcionarios.dao.impl.FuncionarioDAOImpl;
import com.gestion.funcionarios.exception.DAOException;
import com.gestion.funcionarios.exception.UnauthorizedException;
import com.gestion.funcionarios.exception.ValidationException;
import com.gestion.funcionarios.model.Funcionario;
import com.gestion.funcionarios.security.PasswordUtil;
import com.gestion.funcionarios.security.Role;
import com.gestion.funcionarios.security.SessionContext;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de negocio para el módulo de Funcionarios.
 *
 * Reglas de acceso (token requerido en todos los métodos):
 *   ADMINISTRADOR : CRUD completo.
 *   DOCENTE       : sin acceso (solo puede visualizar inventarios).
 */
public class FuncionarioService {

    private final FuncionarioDAO    funcionarioDAO;
    private final SessionContext    session;

    public FuncionarioService() {
        this.funcionarioDAO = new FuncionarioDAOImpl();
        this.session        = SessionContext.getInstance();
    }

    public FuncionarioService(FuncionarioDAO funcionarioDAO) {
        this.funcionarioDAO = funcionarioDAO;
        this.session        = SessionContext.getInstance();
    }

    // ── Lectura ─────────────────────────────────────────────────────────────

    public List<Funcionario> findAll() throws DAOException, UnauthorizedException {
        requireAdmin();
        return funcionarioDAO.findAll();
    }

    public Optional<Funcionario> findById(int id) throws DAOException, UnauthorizedException {
        requireAdmin();
        return funcionarioDAO.findById(id);
    }

    public List<Funcionario> findByTexto(String texto) throws DAOException, UnauthorizedException {
        requireAdmin();
        return funcionarioDAO.findByTexto(texto);
    }

    // ── Escritura ────────────────────────────────────────────────────────────

    /**
     * Crea un nuevo funcionario.
     * La contraseña se recibe en texto plano y se hashea antes de persistir.
     *
     * @param funcionario  objeto con los datos del funcionario.
     * @param plainPassword contraseña en texto plano (obligatoria).
     */
    public Funcionario create(Funcionario funcionario, String plainPassword)
            throws DAOException, UnauthorizedException, ValidationException {
        requireAdmin();
        validatePassword(plainPassword);
        funcionario.setPasswordHash(PasswordUtil.hash(plainPassword));
        if (funcionario.getRol() == null) funcionario.setRol(Role.DOCENTE);
        return funcionarioDAO.save(funcionario);
    }

    /**
     * Actualiza un funcionario existente.
     * Si {@code newPlainPassword} no es null ni vacía, cambia la contraseña.
     *
     * @param funcionario       objeto con los nuevos datos.
     * @param newPlainPassword  nueva contraseña (null = no cambiar).
     */
    public boolean update(Funcionario funcionario, String newPlainPassword)
            throws DAOException, UnauthorizedException, ValidationException {
        requireAdmin();
        if (newPlainPassword != null && !newPlainPassword.isBlank()) {
            validatePassword(newPlainPassword);
            funcionario.setPasswordHash(PasswordUtil.hash(newPlainPassword));
        }
        return funcionarioDAO.update(funcionario);
    }

    public boolean deleteById(int id) throws DAOException, UnauthorizedException {
        requireAdmin();
        return funcionarioDAO.deleteById(id);
    }

    public boolean existsByNumeroDocumento(String doc, int excludeId)
            throws DAOException, UnauthorizedException {
        requireAdmin();
        return funcionarioDAO.existsByNumeroDocumento(doc, excludeId);
    }

    // ── Helpers privados ────────────────────────────────────────────────────

    /**
     * Verifica que el token de sesión sea válido y que el rol sea ADMINISTRADOR.
     * Simula la validación del header "Authorization: Bearer {token}".
     */
    private void requireAdmin() throws UnauthorizedException {
        session.requireToken();
        if (!session.isAdmin()) {
            throw new UnauthorizedException("Gestión de Funcionarios");
        }
    }

    private void validatePassword(String password) throws ValidationException {
        if (password == null || password.length() < 8) {
            throw new ValidationException("Contraseña",
                "La contraseña debe tener mínimo 8 caracteres.");
        }
        boolean hasUpper  = password.chars().anyMatch(Character::isUpperCase);
        boolean hasDigit  = password.chars().anyMatch(Character::isDigit);
        if (!hasUpper || !hasDigit) {
            throw new ValidationException("Contraseña",
                "La contraseña debe incluir al menos una mayúscula y un número.");
        }
    }
}
