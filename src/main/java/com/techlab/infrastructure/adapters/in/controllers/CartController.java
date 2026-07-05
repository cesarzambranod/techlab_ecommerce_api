package com.techlab.infrastructure.adapters.in.controllers;

import com.techlab.application.ports.in.CartUseCasePorts;
import com.techlab.shared.dto.request.CartItemRequestDTO;
import com.techlab.shared.dto.request.UpdateCartItemRequestDTO;
import com.techlab.shared.dto.response.CartResponseDTO;
import com.techlab.shared.dto.response.OrderResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para la gestión del carrito de compras.
 */
@RestController
@RequestMapping("/api/cart")
@Tag(name = "Carrito", description = "Operaciones del carrito de compras")
public class CartController {

    private final CartUseCasePorts cartUseCase;

    public CartController(CartUseCasePorts cartUseCase) {
        this.cartUseCase = cartUseCase;
    }

    @Operation(summary = "Obtener carrito", description = "Obtiene el carrito de compras de un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Carrito obtenido exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<CartResponseDTO> getCart(
            @Parameter(description = "ID del usuario") @PathVariable Long userId) {
        CartResponseDTO cart = cartUseCase.getCart(userId);
        return ResponseEntity.ok(cart);
    }

    @Operation(summary = "Agregar producto al carrito", description = "Agrega un producto al carrito de compras")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto agregado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Stock insuficiente"),
            @ApiResponse(responseCode = "404", description = "Producto o usuario no encontrado")
    })
    @PostMapping("/{userId}/add")
    public ResponseEntity<CartResponseDTO> addItemToCart(
            @Parameter(description = "ID del usuario") @PathVariable Long userId,
            @Valid @RequestBody CartItemRequestDTO request) {
        CartResponseDTO cart = cartUseCase.addItemToCart(userId, request);
        return ResponseEntity.ok(cart);
    }

    @Operation(summary = "Actualizar cantidad", description = "Actualiza la cantidad de un producto en el carrito")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cantidad actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Stock insuficiente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado en el carrito")
    })
    @PutMapping("/{userId}/update/{productId}")
    public ResponseEntity<CartResponseDTO> updateCartItem(
            @Parameter(description = "ID del usuario") @PathVariable Long userId,
            @Parameter(description = "ID del producto") @PathVariable Long productId,
            @Valid @RequestBody UpdateCartItemRequestDTO request) {
        CartResponseDTO cart = cartUseCase.updateCartItem(userId, productId, request.quantity());
        return ResponseEntity.ok(cart);
    }

    @Operation(summary = "Eliminar producto del carrito", description = "Elimina un producto del carrito de compras")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado en el carrito")
    })
    @DeleteMapping("/{userId}/remove/{productId}")
    public ResponseEntity<CartResponseDTO> removeItemFromCart(
            @Parameter(description = "ID del usuario") @PathVariable Long userId,
            @Parameter(description = "ID del producto") @PathVariable Long productId) {
        CartResponseDTO cart = cartUseCase.removeItemFromCart(userId, productId);
        return ResponseEntity.ok(cart);
    }

    @Operation(summary = "Finalizar compra", description = "Convierte el carrito en un pedido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pedido creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Carrito vacío o stock insuficiente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PostMapping("/{userId}/checkout")
    public ResponseEntity<OrderResponseDTO> checkout(
            @Parameter(description = "ID del usuario") @PathVariable Long userId) {
        OrderResponseDTO order = cartUseCase.checkout(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }
}
