package com.techlab.infrastructure.adapters.out.repositories;

import com.techlab.application.ports.out.CartRepositoryPort;
import com.techlab.domain.model.Cart;
import com.techlab.domain.model.CartItem;
import com.techlab.domain.model.Product;
import com.techlab.infrastructure.adapters.out.entities.CartItemEntity;
import com.techlab.infrastructure.adapters.out.entities.ProductEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adaptador que implementa el puerto de persistencia del carrito.
 */
@Component
public class CartRepositoryAdapter implements CartRepositoryPort {

    private final SpringDataCartRepository springDataCartRepository;
    private final SpringDataProductRepository springDataProductRepository;

    public CartRepositoryAdapter(SpringDataCartRepository springDataCartRepository,
                                  SpringDataProductRepository springDataProductRepository) {
        this.springDataCartRepository = springDataCartRepository;
        this.springDataProductRepository = springDataProductRepository;
    }

    @Override
    public Cart getCartByUserId(Long userId) {
        List<CartItem> items = findByUserId(userId);
        return Cart.builder()
                .userId(userId)
                .items(items)
                .build();
    }

    @Override
    public CartItem saveCartItem(CartItem cartItem) {
        CartItemEntity entity = mapToEntity(cartItem);
        CartItemEntity saved = springDataCartRepository.save(entity);
        return mapToDomain(saved);
    }

    @Override
    public Optional<CartItem> findCartItemByUserIdAndProductId(Long userId, Long productId) {
        return springDataCartRepository.findByUserIdAndProductId(userId, productId)
                .map(this::mapToDomain);
    }

    @Override
    public List<CartItem> findByUserId(Long userId) {
        return springDataCartRepository.findByUserId(userId)
                .stream()
                .map(entity -> {
                    CartItem item = mapToDomain(entity);
                    springDataProductRepository.findByIdAndDeletedFalse(entity.getProductId())
                            .ifPresent(productEntity -> item.setProduct(mapProductToDomain(productEntity)));
                    return item;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void deleteCartItem(Long cartItemId) {
        springDataCartRepository.deleteById(cartItemId);
    }

    @Override
    public void deleteAllByUserId(Long userId) {
        springDataCartRepository.deleteByUserId(userId);
    }

    @Override
    public void updateCartItemQuantity(Long cartItemId, Integer quantity) {
        springDataCartRepository.updateQuantity(cartItemId, quantity);
    }

    private CartItem mapToDomain(CartItemEntity entity) {
        return CartItem.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .productId(entity.getProductId())
                .quantity(entity.getQuantity())
                .unitPrice(entity.getUnitPrice())
                .build();
    }

    private CartItemEntity mapToEntity(CartItem item) {
        return CartItemEntity.builder()
                .id(item.getId())
                .userId(item.getUserId())
                .productId(item.getProductId())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .build();
    }

    private Product mapProductToDomain(ProductEntity entity) {
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
}
