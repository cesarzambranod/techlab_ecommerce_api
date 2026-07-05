package com.techlab.application.ports.in;

import com.techlab.shared.dto.request.CartItemRequestDTO;
import com.techlab.shared.dto.response.CartResponseDTO;
import com.techlab.shared.dto.response.OrderResponseDTO;

import java.util.List;

/**
 * Puerto de entrada para operaciones del carrito de compras.
 */
public interface CartUseCasePorts {

    /**
     * Obtiene el carrito de un usuario.
     *
     * @param userId ID del usuario
     * @return El carrito con sus items
     */
    CartResponseDTO getCart(Long userId);

    /**
     * Agrega un producto al carrito.
     *
     * @param userId ID del usuario
     * @param request DTO con el producto y cantidad a agregar
     * @return El carrito actualizado
     */
    CartResponseDTO addItemToCart(Long userId, CartItemRequestDTO request);

    /**
     * Actualiza la cantidad de un producto en el carrito.
     *
     * @param userId ID del usuario
     * @param productId ID del producto
     * @param quantity Nueva cantidad
     * @return El carrito actualizado
     */
    CartResponseDTO updateCartItem(Long userId, Long productId, Integer quantity);

    /**
     * Elimina un producto del carrito.
     *
     * @param userId ID del usuario
     * @param productId ID del producto a eliminar
     * @return El carrito actualizado
     */
    CartResponseDTO removeItemFromCart(Long userId, Long productId);

    /**
     * Convierte el carrito en un pedido.
     *
     * @param userId ID del usuario
     * @return El pedido creado
     */
    OrderResponseDTO checkout(Long userId);

    /**
     * Limpia el carrito de un usuario.
     *
     * @param userId ID del usuario
     */
    void clearCart(Long userId);
}
