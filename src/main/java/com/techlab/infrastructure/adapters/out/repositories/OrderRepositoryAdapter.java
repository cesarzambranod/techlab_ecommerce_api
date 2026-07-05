package com.techlab.infrastructure.adapters.out.repositories;

import com.techlab.application.ports.out.OrderRepositoryPort;
import com.techlab.domain.enums.OrderStatus;
import com.techlab.domain.model.Order;
import com.techlab.domain.model.OrderItem;
import com.techlab.infrastructure.adapters.out.entities.OrderEntity;
import com.techlab.infrastructure.adapters.out.entities.OrderItemEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adaptador que implementa el puerto de persistencia de pedidos.
 */
@Component
public class OrderRepositoryAdapter implements OrderRepositoryPort {

    private final SpringDataOrderRepository springDataRepository;

    public OrderRepositoryAdapter(SpringDataOrderRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public Order save(Order order) {
        OrderEntity entity = mapToEntity(order);
        OrderEntity saved = springDataRepository.save(entity);
        return mapToDomain(saved);
    }

    @Override
    public Optional<Order> findById(Long id) {
        return springDataRepository.findByIdWithItems(id)
                .map(this::mapToDomain);
    }

    @Override
    public List<Order> findAll() {
        return springDataRepository.findAllWithItems()
                .stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> findByUserId(Long userId) {
        return springDataRepository.findByUserIdWithItems(userId)
                .stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> findByStatus(OrderStatus status) {
        return springDataRepository.findByStatus(status)
                .stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void updateStatus(Long orderId, OrderStatus status) {
        springDataRepository.updateStatus(orderId, status);
    }

    private Order mapToDomain(OrderEntity entity) {
        List<OrderItem> items = entity.getItems().stream()
                .map(this::mapItemToDomain)
                .collect(Collectors.toList());

        return Order.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .total(entity.getTotal())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .items(items)
                .build();
    }

    private OrderItem mapItemToDomain(OrderItemEntity entity) {
        return OrderItem.builder()
                .id(entity.getId())
                .orderId(entity.getOrder().getId())
                .productId(entity.getProductId())
                .quantity(entity.getQuantity())
                .unitPrice(entity.getUnitPrice())
                .subtotal(entity.getSubtotal())
                .build();
    }

    private OrderEntity mapToEntity(Order order) {
        OrderEntity.OrderEntityBuilder builder = OrderEntity.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .total(order.getTotal())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt());

        if (order.getItems() != null) {
            List<OrderItemEntity> itemEntities = order.getItems().stream()
                    .map(item -> mapItemToEntity(item, builder.build()))
                    .collect(Collectors.toList());
            builder.items(itemEntities);
        }

        return builder.build();
    }

    private OrderItemEntity mapItemToEntity(OrderItem item, OrderEntity order) {
        return OrderItemEntity.builder()
                .id(item.getId())
                .order(order)
                .productId(item.getProductId())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .subtotal(item.getSubtotal())
                .build();
    }
}
