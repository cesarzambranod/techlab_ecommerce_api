package com.techlab.infrastructure.adapters.out.repositories;

import com.techlab.infrastructure.adapters.out.entities.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio Spring Data para productos.
 */
@Repository
public interface SpringDataProductRepository extends JpaRepository<ProductEntity, Long> {

    Optional<ProductEntity> findByIdAndDeletedFalse(Long id);

    List<ProductEntity> findByDeletedFalse();

    List<ProductEntity> findByCategoryAndDeletedFalse(String category);

    boolean existsByIdAndDeletedFalse(Long id);

    @Query("SELECT p FROM ProductEntity p WHERE (LOWER(p.name) LIKE LOWER(CONCAT('%', :searchText, '%')) OR LOWER(p.category) LIKE LOWER(CONCAT('%', :searchText, '%'))) AND p.deleted = false")
    List<ProductEntity> searchByNameOrCategory(@Param("searchText") String searchText);

    @Modifying
    @Query("UPDATE ProductEntity p SET p.deleted = true WHERE p.id = :id")
    void softDelete(@Param("id") Long id);
}
