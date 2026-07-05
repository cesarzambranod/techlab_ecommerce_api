package com.techlab.infrastructure.adapters.out.repositories;

import com.techlab.infrastructure.adapters.out.entities.CartItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio Spring Data para items del carrito.
 */
@Repository
public interface SpringDataCartRepository extends JpaRepository<CartItemEntity, Long> {

    List<CartItemEntity> findByUserId(Long userId);

    Optional<CartItemEntity> findByUserIdAndProductId(Long userId, Long productId);

    void deleteByUserId(Long userId);

    @Modifying
    @Query("UPDATE CartItemEntity c SET c.quantity = :quantity WHERE c.id = :id")
    void updateQuantity(@Param("id") Long id, @Param("quantity") Integer quantity);
}
