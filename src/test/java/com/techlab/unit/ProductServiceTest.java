package com.techlab.unit;

import com.techlab.application.ports.out.ProductRepositoryPort;
import com.techlab.application.services.ProductService;
import com.techlab.domain.exceptions.ProductNotFoundException;
import com.techlab.domain.model.Product;
import com.techlab.shared.dto.request.ProductRequestDTO;
import com.techlab.shared.dto.response.ProductResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitarios para ProductService")
class ProductServiceTest {

    @Mock
    private ProductRepositoryPort productRepository;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;
    private ProductRequestDTO testRequest;

    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .id(1L)
                .name("Laptop Test")
                .description("Descripción de prueba")
                .price(new BigDecimal("1000.00"))
                .category("Electrónica")
                .imageUrl("https://example.com/image.jpg")
                .stock(10)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build();

        testRequest = new ProductRequestDTO(
                "Laptop Test",
                "Descripción de prueba",
                new BigDecimal("1000.00"),
                "Electrónica",
                "https://example.com/image.jpg",
                10
        );
    }

    @Test
    @DisplayName("Debe crear un producto exitosamente")
    void createProduct_Success() {
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        ProductResponseDTO result = productService.createProduct(testRequest);

        assertNotNull(result);
        assertEquals("Laptop Test", result.name());
        assertEquals(new BigDecimal("1000.00"), result.price());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Debe obtener un producto por ID")
    void getProductById_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        ProductResponseDTO result = productService.getProductById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Laptop Test", result.name());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el producto no existe")
    void getProductById_NotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.getProductById(99L));
    }

    @Test
    @DisplayName("Debe listar todos los productos")
    void getAllProducts_Success() {
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.findAll()).thenReturn(products);

        List<ProductResponseDTO> result = productService.getAllProducts();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Laptop Test", result.get(0).name());
    }

    @Test
    @DisplayName("Debe actualizar un producto exitosamente")
    void updateProduct_Success() {
        ProductRequestDTO updateRequest = new ProductRequestDTO(
                "Laptop Actualizada",
                "Nueva descripción",
                new BigDecimal("1200.00"),
                "Electrónica",
                "https://example.com/new-image.jpg",
                15
        );

        Product updatedProduct = Product.builder()
                .id(1L)
                .name("Laptop Actualizada")
                .description("Nueva descripción")
                .price(new BigDecimal("1200.00"))
                .category("Electrónica")
                .imageUrl("https://example.com/new-image.jpg")
                .stock(15)
                .createdAt(testProduct.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        ProductResponseDTO result = productService.updateProduct(1L, updateRequest);

        assertNotNull(result);
        assertEquals("Laptop Actualizada", result.name());
        assertEquals(new BigDecimal("1200.00"), result.price());
    }

    @Test
    @DisplayName("Debe eliminar un producto (soft delete)")
    void deleteProduct_Success() {
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L);

        assertDoesNotThrow(() -> productService.deleteProduct(1L));
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Debe buscar productos por nombre o categoría")
    void searchProducts_Success() {
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.searchByNameOrCategory("Laptop")).thenReturn(products);

        List<ProductResponseDTO> result = productService.searchProducts("Laptop");

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Debe filtrar productos por categoría")
    void getProductsByCategory_Success() {
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.findByCategory("Electrónica")).thenReturn(products);

        List<ProductResponseDTO> result = productService.getProductsByCategory("Electrónica");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Electrónica", result.get(0).category());
    }
}
