package com.techlab.infrastructure.adapters.in.controllers;

import com.techlab.application.ports.in.OrderUseCasePorts;
import com.techlab.shared.dto.request.OrderRequestDTO;
import com.techlab.shared.dto.request.UpdateOrderStatusRequestDTO;
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

import java.util.List;

/**
 * Controlador REST para la gestión de pedidos.
 */
@RestController
@RequestMapping("/api/orders")
@Tag(name = "Pedidos", description = "Operaciones de gestión de pedidos")
public class OrderController {

    private final OrderUseCasePorts orderUseCase;

    public OrderController(OrderUseCasePorts orderUseCase) {
        this.orderUseCase = orderUseCase;
    }

    @Operation(summary = "Listar todos los pedidos", description = "Obtiene una lista de todos los pedidos (ADMIN)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de pedidos obtenida exitosamente")
    })
    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        List<OrderResponseDTO> orders = orderUseCase.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @Operation(summary = "Obtener pedido por ID", description = "Obtiene los detalles de un pedido específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrderById(
            @Parameter(description = "ID del pedido") @PathVariable Long id) {
        OrderResponseDTO order = orderUseCase.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @Operation(summary = "Historial de pedidos de usuario", description = "Obtiene el historial de pedidos de un usuario específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial de pedidos obtenido"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByUserId(
            @Parameter(description = "ID del usuario") @PathVariable Long userId) {
        List<OrderResponseDTO> orders = orderUseCase.getOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    @Operation(summary = "Crear nuevo pedido", description = "Crea un nuevo pedido para un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pedido creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Stock insuficiente o datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(
            @Valid @RequestBody OrderRequestDTO request) {
        OrderResponseDTO created = orderUseCase.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Actualizar estado del pedido", description = "Actualiza el estado de un pedido existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado"),
            @ApiResponse(responseCode = "400", description = "Estado inválido")
    })
    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(
            @Parameter(description = "ID del pedido") @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequestDTO request) {
        OrderResponseDTO updated = orderUseCase.updateOrderStatus(id, request.status());
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Cancelar pedido", description = "Cancela un pedido existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Pedido cancelado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado"),
            @ApiResponse(responseCode = "400", description = "No se puede cancelar el pedido")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelOrder(
            @Parameter(description = "ID del pedido") @PathVariable Long id) {
        orderUseCase.cancelOrder(id);
        return ResponseEntity.noContent().build();
    }
}
