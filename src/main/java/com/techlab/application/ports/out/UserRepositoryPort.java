package com.techlab.application.ports.out;

import com.techlab.domain.enums.UserRole;
import com.techlab.domain.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para operaciones de persistencia de usuarios.
 */
public interface UserRepositoryPort {

    /**
     * Guarda un usuario en la base de datos.
     *
     * @param user Usuario a guardar
     * @return El usuario guardado
     */
    User save(User user);

    /**
     * Busca un usuario por su ID.
     *
     * @param id ID del usuario
     * @return Optional con el usuario si existe
     */
    Optional<User> findById(Long id);

    /**
     * Busca un usuario por su nombre de usuario.
     *
     * @param username Nombre de usuario
     * @return Optional con el usuario si existe
     */
    Optional<User> findByUsername(String username);

    /**
     * Busca un usuario por su email.
     *
     * @param email Email del usuario
     * @return Optional con el usuario si existe
     */
    Optional<User> findByEmail(String email);

    /**
     * Lista todos los usuarios.
     *
     * @return Lista de usuarios
     */
    List<User> findAll();

    /**
     * Lista usuarios por rol.
     *
     * @param role Rol a filtrar
     * @return Lista de usuarios con el rol especificado
     */
    List<User> findByRole(UserRole role);

    /**
     * Verifica si existe un nombre de usuario.
     *
     * @param username Nombre de usuario
     * @return true si existe
     */
    boolean existsByUsername(String username);

    /**
     * Verifica si existe un email.
     *
     * @param email Email a verificar
     * @return true si existe
     */
    boolean existsByEmail(String email);
}
