package com.techlab.application.services;

import com.techlab.application.ports.in.CartUseCasePorts;
import com.techlab.application.ports.out.CartRepositoryPort;
import com.techlab.application.ports.out.OrderRepositoryPort;
import com.techlab.application.ports.out.ProductRepositoryPort;
import com.techlab.application.ports.out.UserRepositoryPort;
import com.techlab.domain.enums.OrderStatus;
import com.techlab.domain.exceptions.BusinessException;
import com.techlab.domain.exceptions.InsufficientStockException;
import com.techlab.domain.exceptions.ProductNotFoundException;
import com.techlab.domain.exceptions.UserNotFoundException;
import com.techlab.domain.model.*;
import com.techlab.shared.dto.request.CartItemRequestDTO;
import com.techlab.shared.dto.response.CartItemResponseDTO;
import com.techlab.shared.dto.response.CartResponseDTO;
import com.techlab.shared.dto.response.OrderItemResponseDTO;
import com.techlab.shared.dto.response.OrderResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio que implementa los casos de uso del carrito de compras.
 */
@Service
@Transactional
public class CartService implements CartUseCasePorts {

    private final CartRepositoryPort cartRepository;
    private final ProductRepositoryPort productRepository;
    private final UserRepositoryPort userRepository;
    private final OrderRepositoryPort orderRepository;

    public CartService(CartRepositoryPort cartRepository,
                       ProductRepositoryPort productRepository,
                       UserRepositoryPort userRepository,
                       OrderRepositoryPort orderRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public CartResponseDTO getCart(Long userId) {
        if (!userRepository.findById(userId).isPresent()) {
            throw new UserNotFoundException(userId);
        }
        return mapToCartResponseDTO(cartRepository.getCartByUserId(userId));
    }

    @Override
    public CartResponseDTO addItemToCart(Long userId, CartItemRequestDTO request) {
        if (!userRepository.findById(userId).isPresent()) {
            throw new UserNotFoundException(userId);
        }

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ProductNotFoundException(request.productId()));

        if (product.getStock() < request.quantity()) {
            throw new InsufficientStockException(product.getId(), product.getStock(), request.quantity());
        }

        var existingItem = cartRepository.findCartItemByUserIdAndProductId(userId, request.productId());

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + request.quantity();

            if (product.getStock() < newQuantity) {
                throw new InsufficientStockException(product.getId(), product.getStock(), newQuantity);
            }

            cartRepository.updateCartItemQuantity(item.getId(), newQuantity);
        } else {
            CartItem newItem = CartItem.builder()
                    .userId(userId)
                    .productId(product.getId())
                    .quantity(request.quantity())
                    .unitPrice(product.getPrice())
                    .build();
            cartRepository.saveCartItem(newItem);
        }

        return mapToCartResponseDTO(cartRepository.getCartByUserId(userId));
    }

    @Override
    public CartResponseDTO updateCartItem(Long userId, Long productId, Integer quantity) {
        if (!userRepository.findById(userId).isPresent()) {
            throw new UserNotFoundException(userId);
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        if (product.getStock() < quantity) {
            throw new InsufficientStockException(productId, product.getStock(), quantity);
        }

        CartItem item = cartRepository.findCartItemByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new BusinessException("Item no encontrado en el carrito"));

        cartRepository.updateCartItemQuantity(item.getId(), quantity);

        return mapToCartResponseDTO(cartRepository.getCartByUserId(userId));
    }

    @Override
    public CartResponseDTO removeItemFromCart(Long userId, Long productId) {
        if (!userRepository.findById(userId).isPresent()) {
            throw new UserNotFoundException(userId);
        }

        CartItem item = cartRepository.findCartItemByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new BusinessException("Item no encontrado en el carrito"));

        cartRepository.deleteCartItem(item.getId());

        return mapToCartResponseDTO(cartRepository.getCartByUserId(userId));
    }

    @Override
    public OrderResponseDTO checkout(Long userId) {
        if (!userRepository.findById(userId).isPresent()) {
            throw new UserNotFoundException(userId);
        }

        Cart cart = cartRepository.getCartByUserId(userId);

        if (cart.getItems().isEmpty()) {
            throw new BusinessException("El carrito está vacío");
        }

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {
            Product product = productRepository.findById(cartItem.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException(cartItem.getProductId()));

            if (product.getStock() < cartItem.getQuantity()) {
                throw new InsufficientStockException(
                        product.getId(),
                        product.getStock(),
                        cartItem.getQuantity());
            }

            BigDecimal subtotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            total = total.add(subtotal);

            OrderItem orderItem = OrderItem.builder()
                    .productId(product.getId())
                    .quantity(cartItem.getQuantity())
                    .unitPrice(product.getPrice())
                    .subtotal(subtotal)
                    .build();

            orderItems.add(orderItem);

            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);
        }

        Order order = Order.builder()
                .userId(userId)
                .total(total)
                .status(OrderStatus.PENDIENTE)
                .items(orderItems)
                .build();

        Order saved = orderRepository.save(order);

        clearCart(userId);

        return mapToOrderResponseDTO(saved);
    }

    @Override
    public void clearCart(Long userId) {
        cartRepository.deleteAllByUserId(userId);
    }

    private CartResponseDTO mapToCartResponseDTO(Cart cart) {
        List<CartItemResponseDTO> itemDTOs = cart.getItems().stream()
                .map(item -> {
                    Product product = item.getProduct();
                    String productName = product != null ? product.getName() : "Producto no encontrado";
                    String imageUrl = product != null ? product.getImageUrl() : null;
                    BigDecimal subtotal = item.getUnitPrice()
                            .multiply(BigDecimal.valueOf(item.getQuantity()));

                    return new CartItemResponseDTO(
                            item.getId(),
                            item.getProductId(),
                            productName,
                            imageUrl,
                            item.getQuantity(),
                            item.getUnitPrice(),
                            subtotal
                    );
                })
                .collect(Collectors.toList());

        return new CartResponseDTO(
                cart.getUserId(),
                itemDTOs,
                cart.calculateTotal()
        );
    }

    private OrderResponseDTO mapToOrderResponseDTO(Order order) {
        List<OrderItemResponseDTO> itemDTOs = order.getItems().stream()
                .map(item -> {
                    String productName = productRepository.findById(item.getProductId())
                            .map(Product::getName)
                            .orElse("Producto no encontrado");

                    return new OrderItemResponseDTO(
                            item.getId(),
                            item.getProductId(),
                            productName,
                            item.getQuantity(),
                            item.getUnitPrice(),
                            item.getSubtotal()
                    );
                })
                .collect(Collectors.toList());

        return new OrderResponseDTO(
                order.getId(),
                order.getUserId(),
                order.getTotal(),
                order.getStatus(),
                itemDTOs,
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}
