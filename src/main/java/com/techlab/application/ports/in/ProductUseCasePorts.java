package com.techlab.application.ports.in;

import com.techlab.domain.model.Product;
import com.techlab.shared.dto.request.ProductRequestDTO;
import com.techlab.shared.dto.response.ProductResponseDTO;

import java.util.List;

/**
 * Puerto de entrada para operaciones de gestión de productos.
 */
public interface ProductUseCasePorts {

    /**
     * Crea un nuevo producto en el catálogo.
     *
     * @param request DTO con los datos del producto a crear
     * @return El producto creado
     */
    ProductResponseDTO createProduct(ProductRequestDTO request);

    /**
     * Obtiene un producto por su ID.
     *
     * @param id ID del producto a obtener
     * @return El producto encontrado
     */
    ProductResponseDTO getProductById(Long id);

    /**
     * Lista todos los productos disponibles.
     *
     * @return Lista de productos
     */
    List<ProductResponseDTO> getAllProducts();

    /**
     * Actualiza un producto existente.
     *
     * @param id ID del producto a actualizar
     * @param request DTO con los nuevos datos del producto
     * @return El producto actualizado
     */
    ProductResponseDTO updateProduct(Long id, ProductRequestDTO request);

    /**
     * Elimina un producto de forma lógica.
     *
     * @param id ID del producto a eliminar
     */
    void deleteProduct(Long id);

    /**
     * Busca productos por nombre o categoría.
     *
     * @param searchText Texto de búsqueda
     * @return Lista de productos que coinciden
     */
    List<ProductResponseDTO> searchProducts(String searchText);

    /**
     * Filtra productos por categoría.
     *
     * @param category Categoría a filtrar
     * @return Lista de productos de la categoría
     */
    List<ProductResponseDTO> getProductsByCategory(String category);

    /**
     * Obtiene un producto como entidad de dominio.
     *
     * @param id ID del producto
     * @return Entidad Product
     */
    Product getProductEntityById(Long id);
}
