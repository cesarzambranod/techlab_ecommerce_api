package com.techlab.application.services;

import com.techlab.application.ports.in.ProductUseCasePorts;
import com.techlab.application.ports.out.ProductRepositoryPort;
import com.techlab.domain.exceptions.ProductNotFoundException;
import com.techlab.domain.model.Product;
import com.techlab.shared.dto.request.ProductRequestDTO;
import com.techlab.shared.dto.response.ProductResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio que implementa los casos de uso de productos.
 * Utiliza características de Java 21: .toList() nativo, pattern matching.
 */
@Service
@Transactional
public class ProductService implements ProductUseCasePorts {

    private final ProductRepositoryPort productRepository;

    public ProductService(ProductRepositoryPort productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public ProductResponseDTO createProduct(ProductRequestDTO request) {
        Product product = Product.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .category(request.category())
                .imageUrl(request.imageUrl())
                .stock(request.stock())
                .deleted(false)
                .build();

        Product saved = productRepository.save(product);
        return mapToResponseDTO(saved);
    }

    @Override
    public ProductResponseDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return mapToResponseDTO(product);
    }

    @Override
    public List<ProductResponseDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .toList(); // Java 21+ : SequencedCollection
    }

    @Override
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO request) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        Product updated = Product.builder()
                .id(existing.getId())
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .category(request.category())
                .imageUrl(request.imageUrl())
                .stock(request.stock())
                .createdAt(existing.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .deleted(existing.getDeleted())
                .build();

        Product saved = productRepository.save(updated);
        return mapToResponseDTO(saved);
    }

    @Override
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }
        productRepository.deleteById(id);
    }

    @Override
    public List<ProductResponseDTO> searchProducts(String searchText) {
        return productRepository.searchByNameOrCategory(searchText).stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    public List<ProductResponseDTO> getProductsByCategory(String category) {
        return productRepository.findByCategory(category).stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    public Product getProductEntityById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    private ProductResponseDTO mapToResponseDTO(Product product) {
        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCategory(),
                product.getImageUrl(),
                product.getStock(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}
