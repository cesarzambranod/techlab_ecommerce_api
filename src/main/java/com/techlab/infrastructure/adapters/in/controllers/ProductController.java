package com.techlab.infrastructure.adapters.in.controllers;

import com.techlab.application.ports.in.ProductUseCasePorts;
import com.techlab.shared.dto.request.ProductRequestDTO;
import com.techlab.shared.dto.response.ProductResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de productos.
 */
@RestController
@RequestMapping("/api/products")
@Tag(name = "Productos", description = "Operaciones de gestión de productos")
public class ProductController {

    private final ProductUseCasePorts productUseCase;

    public ProductController(ProductUseCasePorts productUseCase) {
        this.productUseCase = productUseCase;
    }

    @Operation(summary = "Listar todos los productos", description = "Obtiene una lista de todos los productos disponibles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de productos obtenida exitosamente")
    })
    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        List<ProductResponseDTO> products = productUseCase.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Obtener producto por ID", description = "Obtiene los detalles de un producto específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(
            @Parameter(description = "ID del producto") @PathVariable Long id) {
        ProductResponseDTO product = productUseCase.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @Operation(summary = "Crear nuevo producto", description = "Agrega un nuevo producto al catálogo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Producto creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos del producto inválidos")
    })
    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(
            @Valid @RequestBody ProductRequestDTO request) {
        ProductResponseDTO created = productUseCase.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Actualizar producto", description = "Actualiza la información de un producto existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "400", description = "Datos del producto inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @Parameter(description = "ID del producto") @PathVariable Long id,
            @Valid @RequestBody ProductRequestDTO request) {
        ProductResponseDTO updated = productUseCase.updateProduct(id, request);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Eliminar producto", description = "Elimina un producto del catálogo (soft delete)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Producto eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "ID del producto") @PathVariable Long id) {
        productUseCase.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Buscar productos", description = "Busca productos por nombre o categoría")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resultados de búsqueda obtenidos")
    })
    @GetMapping("/search")
    public ResponseEntity<List<ProductResponseDTO>> searchProducts(
            @Parameter(description = "Texto de búsqueda") @RequestParam String searchText) {
        List<ProductResponseDTO> products = productUseCase.searchProducts(searchText);
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Filtrar por categoría", description = "Obtiene productos de una categoría específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Productos de la categoría obtenidos")
    })
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductResponseDTO>> getProductsByCategory(
            @Parameter(description = "Nombre de la categoría") @PathVariable String category) {
        List<ProductResponseDTO> products = productUseCase.getProductsByCategory(category);
        return ResponseEntity.ok(products);
    }
}
