package com.techlab.application.ports.out;

import com.techlab.domain.enums.OrderStatus;
import com.techlab.domain.model.Order;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para operaciones de persistencia de pedidos.
 */
public interface OrderRepositoryPort {

    /**
     * Guarda un pedido en la base de datos.
     *
     * @param order Pedido a guardar
     * @return El pedido guardado
     */
    Order save(Order order);

    /**
     * Busca un pedido por su ID.
     *
     * @param id ID del pedido
     * @return Optional con el pedido si existe
     */
    Optional<Order> findById(Long id);

    /**
     * Lista todos los pedidos.
     *
     * @return Lista de pedidos
     */
    List<Order> findAll();

    /**
     * Lista pedidos por ID de usuario.
     *
     * @param userId ID del usuario
     * @return Lista de pedidos del usuario
     */
    List<Order> findByUserId(Long userId);

    /**
     * Lista pedidos por estado.
     *
     * @param status Estado del pedido
     * @return Lista de pedidos con el estado especificado
     */
    List<Order> findByStatus(OrderStatus status);

    /**
     * Actualiza el estado de un pedido.
     *
     * @param orderId ID del pedido
     * @param status Nuevo estado
     */
    void updateStatus(Long orderId, OrderStatus status);
}
