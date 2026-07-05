package com.techlab.application.ports.out;

import com.techlab.domain.model.Cart;
import com.techlab.domain.model.CartItem;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para operaciones de persistencia del carrito.
 */
public interface CartRepositoryPort {

    /**
     * Obtiene el carrito de un usuario.
     *
     * @param userId ID del usuario
     * @return El carrito del usuario
     */
    Cart getCartByUserId(Long userId);

    /**
     * Agrega un item al carrito.
     *
     * @param cartItem Item a agregar
     * @return El item agregado
     */
    CartItem saveCartItem(CartItem cartItem);

    /**
     * Busca un item del carrito por usuario y producto.
     *
     * @param userId ID del usuario
     * @param productId ID del producto
     * @return Optional con el item si existe
     */
    Optional<CartItem> findCartItemByUserIdAndProductId(Long userId, Long productId);

    /**
     * Lista los items del carrito de un usuario.
     *
     * @param userId ID del usuario
     * @return Lista de items
     */
    List<CartItem> findByUserId(Long userId);

    /**
     * Elimina un item del carrito.
     *
     * @param cartItemId ID del item a eliminar
     */
    void deleteCartItem(Long cartItemId);

    /**
     * Elimina todos los items del carrito de un usuario.
     *
     * @param userId ID del usuario
     */
    void deleteAllByUserId(Long userId);

    /**
     * Actualiza la cantidad de un item del carrito.
     *
     * @param cartItemId ID del item
     * @param quantity Nueva cantidad
     */
    void updateCartItemQuantity(Long cartItemId, Integer quantity);
}
