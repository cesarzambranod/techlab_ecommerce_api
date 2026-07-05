package com.techlab.application.ports.in;

import com.techlab.shared.dto.request.UserRequestDTO;
import com.techlab.shared.dto.response.UserResponseDTO;

import java.util.List;

/**
 * Puerto de entrada para operaciones de gestión de usuarios.
 */
public interface UserUseCasePorts {

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param request DTO con los datos del usuario
     * @return El usuario creado
     */
    UserResponseDTO registerUser(UserRequestDTO request);

    /**
     * Autentica un usuario y retorna un token JWT.
     *
     * @param username Nombre de usuario
     * @param password Contraseña
     * @return Token JWT si la autenticación es exitosa
     */
    String authenticateUser(String username, String password);

    /**
     * Obtiene un usuario por su ID.
     *
     * @param id ID del usuario
     * @return El usuario encontrado
     */
    UserResponseDTO getUserById(Long id);

    /**
     * Lista todos los usuarios (solo para administradores).
     *
     * @return Lista de usuarios
     */
    List<UserResponseDTO> getAllUsers();

    /**
     * Obtiene un usuario por su nombre de usuario.
     *
     * @param username Nombre de usuario
     * @return El usuario encontrado
     */
    UserResponseDTO getUserByUsername(String username);
}
