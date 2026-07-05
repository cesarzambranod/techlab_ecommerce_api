package com.techlab.application.ports.out;

import com.techlab.domain.model.Product;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para operaciones de persistencia de productos.
 */
public interface ProductRepositoryPort {

    /**
     * Guarda un producto en la base de datos.
     *
     * @param product Producto a guardar
     * @return El producto guardado
     */
    Product save(Product product);

    /**
     * Busca un producto por su ID.
     *
     * @param id ID del producto
     * @return Optional con el producto si existe
     */
    Optional<Product> findById(Long id);

    /**
     * Lista todos los productos.
     *
     * @return Lista de productos
     */
    List<Product> findAll();

    /**
     * Lista productos que no han sido eliminados lógicamente.
     *
     * @return Lista de productos activos
     */
    List<Product> findByDeletedFalse();

    /**
     * Busca productos por nombre o categoría.
     *
     * @param searchText Texto de búsqueda
     * @return Lista de productos que coinciden
     */
    List<Product> searchByNameOrCategory(String searchText);

    /**
     * Busca productos por categoría.
     *
     * @param category Categoría a buscar
     * @return Lista de productos de la categoría
     */
    List<Product> findByCategory(String category);

    /**
     * Elimina un producto de forma lógica.
     *
     * @param id ID del producto
     */
    void deleteById(Long id);

    /**
     * Verifica si existe un producto por ID.
     *
     * @param id ID del producto
     * @return true si existe
     */
    boolean existsById(Long id);
}
