package com.techlab.application.ports.in;

import com.techlab.shared.dto.request.OrderRequestDTO;
import com.techlab.shared.dto.response.OrderResponseDTO;

import java.util.List;

/**
 * Puerto de entrada para operaciones de gestión de pedidos.
 */
public interface OrderUseCasePorts {

    /**
     * Crea un nuevo pedido para un usuario.
     *
     * @param request DTO con los datos del pedido
     * @return El pedido creado
     */
    OrderResponseDTO createOrder(OrderRequestDTO request);

    /**
     * Obtiene un pedido por su ID.
     *
     * @param id ID del pedido
     * @return El pedido encontrado
     */
    OrderResponseDTO getOrderById(Long id);

    /**
     * Lista todos los pedidos (solo para administradores).
     *
     * @return Lista de pedidos
     */
    List<OrderResponseDTO> getAllOrders();

    /**
     * Obtiene el historial de pedidos de un usuario.
     *
     * @param userId ID del usuario
     * @return Lista de pedidos del usuario
     */
    List<OrderResponseDTO> getOrdersByUserId(Long userId);

    /**
     * Actualiza el estado de un pedido.
     *
     * @param orderId ID del pedido
     * @param status Nuevo estado del pedido
     * @return El pedido actualizado
     */
    OrderResponseDTO updateOrderStatus(Long orderId, String status);

    /**
     * Cancela un pedido.
     *
     * @param orderId ID del pedido a cancelar
     */
    void cancelOrder(Long orderId);
}
