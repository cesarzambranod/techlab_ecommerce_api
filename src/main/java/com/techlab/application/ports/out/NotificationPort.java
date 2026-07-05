package com.techlab.application.ports.out;

/**
 * Puerto de salida para servicios de notificación.
 */
public interface NotificationPort {

    /**
     * Envía una notificación cuando el stock está bajo.
     *
     * @param productId ID del producto
     * @param currentStock Stock actual
     * @param minimumStock Stock mínimo configurado
     */
    void notifyLowStock(Long productId, Integer currentStock, Integer minimumStock);

    /**
     * Envía una notificación cuando se crea un pedido.
     *
     * @param orderId ID del pedido
     * @param userEmail Email del usuario
     */
    void notifyOrderCreated(Long orderId, String userEmail);

    /**
     * Envía una notificación cuando cambia el estado de un pedido.
     *
     * @param orderId ID del pedido
     * @param status Nuevo estado
     * @param userEmail Email del usuario
     */
    void notifyOrderStatusChanged(Long orderId, String status, String userEmail);
}
