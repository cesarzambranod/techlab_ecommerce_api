package com.techlab.infrastructure.adapters.out.repositories;

import com.techlab.domain.enums.UserRole;
import com.techlab.infrastructure.adapters.out.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio Spring Data para usuarios.
 */
@Repository
public interface SpringDataUserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByEmail(String email);

    List<UserEntity> findByRole(UserRole role);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
