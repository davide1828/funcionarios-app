package com.gestion.funcionarios.service;

import com.gestion.funcionarios.dao.FuncionarioDAO;
import com.gestion.funcionarios.dao.impl.FuncionarioDAOImpl;
import com.gestion.funcionarios.exception.AuthException;
import com.gestion.funcionarios.exception.DAOException;
import com.gestion.funcionarios.model.Funcionario;
import com.gestion.funcionarios.security.JwtUtil;
import com.gestion.funcionarios.security.PasswordUtil;
import com.gestion.funcionarios.security.SessionContext;

/**
 * Servicio de autenticación.
 *
 * Expone un único punto de entrada: {@link #login(String, String)}.
 * Si las credenciales son válidas, genera un JWT y lo registra
 * en el {@link SessionContext} (equivale a enviar el token en
 * el header "Authorization: Bearer {token}").
 */
public class AuthService {

    private final FuncionarioDAO funcionarioDAO;

    public AuthService() {
        this.funcionarioDAO = new FuncionarioDAOImpl();
    }

    /** Constructor para inyección de dependencias / testing. */
    public AuthService(FuncionarioDAO funcionarioDAO) {
        this.funcionarioDAO = funcionarioDAO;
    }

    /**
     * Autentica al usuario por email y contraseña.
     *
     * @param email    correo electrónico del funcionario.
     * @param password contraseña en texto plano.
     * @return token JWT generado para la sesión.
     * @throws AuthException si las credenciales son inválidas o el usuario está inactivo.
     * @throws DAOException  si ocurre un error de acceso a datos.
     */
    public String login(String email, String password) throws AuthException, DAOException {
        if (email == null || email.isBlank())    throw new AuthException("El correo es obligatorio.");
        if (password == null || password.isBlank()) throw new AuthException("La contraseña es obligatoria.");

        Funcionario funcionario = funcionarioDAO.findByEmail(email.trim().toLowerCase())
            .orElseThrow(() -> new AuthException("Credenciales incorrectas."));

        if (!"ACTIVO".equals(funcionario.getEstado())) {
            throw new AuthException("El usuario está inactivo. Contacte al administrador.");
        }

        if (!PasswordUtil.verify(password, funcionario.getPasswordHash())) {
            throw new AuthException("Credenciales incorrectas.");
        }

        String token = JwtUtil.generateToken(
            funcionario.getEmail(),
            funcionario.getRol(),
            funcionario.getId()
        );

        // Registrar en el contexto de sesión (equivale al header Bearer en HTTP)
        SessionContext.getInstance().open(token);
        return token;
    }

    /**
     * Cierra la sesión del usuario actual.
     */
    public void logout() {
        SessionContext.getInstance().close();
    }
}
