package com.techlab.unit;

import com.techlab.application.ports.out.OrderRepositoryPort;
import com.techlab.application.ports.out.ProductRepositoryPort;
import com.techlab.application.ports.out.UserRepositoryPort;
import com.techlab.application.services.OrderService;
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
import com.techlab.shared.dto.response.OrderResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitarios para OrderService")
class OrderServiceTest {

    @Mock
    private OrderRepositoryPort orderRepository;

    @Mock
    private ProductRepositoryPort productRepository;

    @Mock
    private UserRepositoryPort userRepository;

    @InjectMocks
    private OrderService orderService;

    private User testUser;
    private Product testProduct;
    private Order testOrder;
    private OrderRequestDTO testRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .build();

        testProduct = Product.builder()
                .id(1L)
                .name("Producto Test")
                .price(new BigDecimal("100.00"))
                .stock(10)
                .deleted(false)
                .build();

        OrderItem orderItem = OrderItem.builder()
                .id(1L)
                .productId(1L)
                .quantity(2)
                .unitPrice(new BigDecimal("100.00"))
                .subtotal(new BigDecimal("200.00"))
                .build();

        testOrder = Order.builder()
                .id(1L)
                .userId(1L)
                .total(new BigDecimal("200.00"))
                .status(OrderStatus.PENDIENTE)
                .items(Arrays.asList(orderItem))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testRequest = new OrderRequestDTO(
                1L,
                Arrays.asList(new OrderItemRequestDTO(1L, 2))
        );
    }

    @Test
    @DisplayName("Debe crear un pedido exitosamente")
    void createOrder_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        OrderResponseDTO result = orderService.createOrder(testRequest);

        assertNotNull(result);
        assertEquals(1L, result.userId());
        assertEquals(OrderStatus.PENDIENTE, result.status());
        verify(productRepository, times(1)).save(any(Product.class));
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el usuario no existe")
    void createOrder_UserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> orderService.createOrder(testRequest));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando no hay stock suficiente")
    void createOrder_InsufficientStock() {
        testProduct.setStock(1);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        assertThrows(InsufficientStockException.class, () -> orderService.createOrder(testRequest));
    }

    @Test
    @DisplayName("Debe obtener un pedido por ID")
    void getOrderById_Success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        OrderResponseDTO result = orderService.getOrderById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(OrderStatus.PENDIENTE, result.status());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el pedido no existe")
    void getOrderById_NotFound() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.getOrderById(99L));
    }

    @Test
    @DisplayName("Debe listar todos los pedidos")
    void getAllOrders_Success() {
        List<Order> orders = Arrays.asList(testOrder);
        when(orderRepository.findAll()).thenReturn(orders);

        List<OrderResponseDTO> result = orderService.getAllOrders();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Debe actualizar el estado de un pedido")
    void updateOrderStatus_Success() {
        Order updatedOrder = Order.builder()
                .id(1L)
                .userId(1L)
                .total(new BigDecimal("200.00"))
                .status(OrderStatus.CONFIRMADO)
                .items(testOrder.getItems())
                .createdAt(testOrder.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(updatedOrder);

        OrderResponseDTO result = orderService.updateOrderStatus(1L, "CONFIRMADO");

        assertNotNull(result);
        assertEquals(OrderStatus.CONFIRMADO, result.status());
    }

    @Test
    @DisplayName("Debe cancelar un pedido exitosamente")
    void cancelOrder_Success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        assertDoesNotThrow(() -> orderService.cancelOrder(1L));
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Debe obtener pedidos por userId")
    void getOrdersByUserId_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(orderRepository.findByUserId(1L)).thenReturn(Arrays.asList(testOrder));

        List<OrderResponseDTO> result = orderService.getOrdersByUserId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
