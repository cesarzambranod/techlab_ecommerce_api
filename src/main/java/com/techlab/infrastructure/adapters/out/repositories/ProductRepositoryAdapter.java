package com.techlab.infrastructure.adapters.out.repositories;

import com.techlab.application.ports.out.ProductRepositoryPort;
import com.techlab.domain.model.Product;
import com.techlab.infrastructure.adapters.out.entities.ProductEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adaptador que implementa el puerto de persistencia de productos.
 */
@Component
public class ProductRepositoryAdapter implements ProductRepositoryPort {

    private final SpringDataProductRepository springDataRepository;

    public ProductRepositoryAdapter(SpringDataProductRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public Product save(Product product) {
        ProductEntity entity = mapToEntity(product);
        ProductEntity saved = springDataRepository.save(entity);
        return mapToDomain(saved);
    }

    @Override
    public Optional<Product> findById(Long id) {
        return springDataRepository.findByIdAndDeletedFalse(id)
                .map(this::mapToDomain);
    }

    @Override
    public List<Product> findAll() {
        return springDataRepository.findByDeletedFalse()
                .stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findByDeletedFalse() {
        return findAll();
    }

    @Override
    public List<Product> searchByNameOrCategory(String searchText) {
        return springDataRepository.searchByNameOrCategory(searchText)
                .stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findByCategory(String category) {
        return springDataRepository.findByCategoryAndDeletedFalse(category)
                .stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        springDataRepository.softDelete(id);
    }

    @Override
    public boolean existsById(Long id) {
        return springDataRepository.existsByIdAndDeletedFalse(id);
    }

    private Product mapToDomain(ProductEntity entity) {
        return Product.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .category(entity.getCategory())
                .imageUrl(entity.getImageUrl())
                .stock(entity.getStock())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deleted(entity.getDeleted())
                .build();
    }

    private ProductEntity mapToEntity(Product product) {
        return ProductEntity.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .category(product.getCategory())
                .imageUrl(product.getImageUrl())
                .stock(product.getStock())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .deleted(product.getDeleted())
                .build();
    }
}
