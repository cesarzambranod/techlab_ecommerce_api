package com.techlab.application.services;

import com.techlab.application.ports.in.OrderUseCasePorts;
import com.techlab.application.ports.out.OrderRepositoryPort;
import com.techlab.application.ports.out.ProductRepositoryPort;
import com.techlab.application.ports.out.UserRepositoryPort;
import com.techlab.domain.enums.OrderStatus;
import com.techlab.domain.exceptions.InsufficientStockException;
import com.techlab.domain.exceptions.OrderNotFoundException;
import com.techlab.domain.exceptions.UserNotFoundException;
import com.techlab.domain.model.Order;
import com.techlab.domain.model.OrderItem;
import com.techlab.domain.model.Product;
import com.techlab.domain.model.User;
import com.techlab.shared.dto.request.OrderItemRequestDTO;
import com.techlab.shared.dto.request.OrderRequestDTO;
import com.techlab.shared.dto.response.OrderItemResponseDTO;
import com.techlab.shared.dto.response.OrderResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.SequencedMap;
import java.util.stream.Collectors;

/**
 * Servicio que implementa los casos de uso de pedidos.
 * Utiliza características de Java 21: pattern matching, sequenced collections, switch expressions.
 */
@Service
@Transactional
public class OrderService implements OrderUseCasePorts {

    private static final int LOW_STOCK_THRESHOLD = 5;

    private final OrderRepositoryPort orderRepository;
    private final ProductRepositoryPort productRepository;
    private final UserRepositoryPort userRepository;

    public OrderService(OrderRepositoryPort orderRepository,
                        ProductRepositoryPort productRepository,
                        UserRepositoryPort userRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    public OrderResponseDTO createOrder(OrderRequestDTO request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new UserNotFoundException(request.userId()));

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequestDTO itemRequest : request.items()) {
            Product product = productRepository.findById(itemRequest.productId())
                    .orElseThrow(() -> new InsufficientStockException(
                            itemRequest.productId(), 0, itemRequest.quantity()));

            if (product.getStock() < itemRequest.quantity()) {
                throw new InsufficientStockException(
                        product.getId(),
                        product.getStock(),
                        itemRequest.quantity());
            }

            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(itemRequest.quantity()));
            total = total.add(subtotal);

            OrderItem orderItem = OrderItem.builder()
                    .productId(product.getId())
                    .quantity(itemRequest.quantity())
                    .unitPrice(product.getPrice())
                    .subtotal(subtotal)
                    .build();

            orderItems.add(orderItem);

            product.setStock(product.getStock() - itemRequest.quantity());
            productRepository.save(product);
        }

        Order order = Order.builder()
                .userId(user.getId())
                .total(total)
                .status(OrderStatus.PENDIENTE)
                .items(orderItems)
                .build();

        Order saved = orderRepository.save(order);
        return mapToResponseDTO(saved);
    }

    @Override
    public OrderResponseDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        return mapToResponseDTO(order);
    }

    @Override
    public List<OrderResponseDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    public List<OrderResponseDTO> getOrdersByUserId(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        return orderRepository.findByUserId(userId).stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    public OrderResponseDTO updateOrderStatus(Long orderId, String statusStr) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        // Pattern matching en switch (Java 21+)
        OrderStatus newStatus = switch (statusStr.toUpperCase()) {
            case "PENDIENTE" -> OrderStatus.PENDIENTE;
            case "CONFIRMADO" -> OrderStatus.CONFIRMADO;
            case "ENVIADO" -> OrderStatus.ENVIADO;
            case "ENTREGADO" -> OrderStatus.ENTREGADO;
            case "CANCELADO" -> OrderStatus.CANCELADO;
            default -> throw new IllegalArgumentException("Estado inválido: " + statusStr);
        };

        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());

        Order updated = orderRepository.save(order);
        return mapToResponseDTO(updated);
    }

    @Override
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        // Pattern matching mejorado para instanceof (Java 16+)
        if (order.getStatus() == OrderStatus.ENTREGADO) {
            throw new IllegalStateException("No se puede cancelar un pedido ya entregado");
        }

        order.getItems().forEach(item -> {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
        });

        order.setStatus(OrderStatus.CANCELADO);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
    }

    private OrderResponseDTO mapToResponseDTO(Order order) {
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
                .toList();

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
