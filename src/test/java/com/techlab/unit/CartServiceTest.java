package com.techlab.unit;

import com.techlab.application.ports.out.CartRepositoryPort;
import com.techlab.application.ports.out.OrderRepositoryPort;
import com.techlab.application.ports.out.ProductRepositoryPort;
import com.techlab.application.ports.out.UserRepositoryPort;
import com.techlab.application.services.CartService;
import com.techlab.domain.enums.OrderStatus;
import com.techlab.domain.enums.UserRole;
import com.techlab.domain.exceptions.BusinessException;
import com.techlab.domain.exceptions.InsufficientStockException;
import com.techlab.domain.exceptions.ProductNotFoundException;
import com.techlab.domain.exceptions.UserNotFoundException;
import com.techlab.domain.model.*;
import com.techlab.shared.dto.request.CartItemRequestDTO;
import com.techlab.shared.dto.response.CartResponseDTO;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitarios para CartService")
class CartServiceTest {

    @Mock
    private CartRepositoryPort cartRepository;

    @Mock
    private ProductRepositoryPort productRepository;

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private OrderRepositoryPort orderRepository;

    @InjectMocks
    private CartService cartService;

    private User testUser;
    private Product testProduct;
    private Cart testCart;
    private CartItem testCartItem;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .role(UserRole.USER)
                .build();

        testProduct = Product.builder()
                .id(1L)
                .name("Producto Test")
                .price(new BigDecimal("100.00"))
                .stock(10)
                .deleted(false)
                .build();

        testCartItem = CartItem.builder()
                .id(1L)
                .userId(1L)
                .productId(1L)
                .quantity(2)
                .unitPrice(new BigDecimal("100.00"))
                .product(testProduct)
                .build();

        testCart = Cart.builder()
                .userId(1L)
                .items(new ArrayList<>(Arrays.asList(testCartItem)))
                .build();
    }

    @Test
    @DisplayName("Debe obtener carrito exitosamente")
    void getCart_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(cartRepository.getCartByUserId(1L)).thenReturn(testCart);

        CartResponseDTO result = cartService.getCart(1L);

        assertNotNull(result);
        assertEquals(1L, result.userId());
        assertEquals(1, result.items().size());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando usuario no existe al obtener carrito")
    void getCart_UserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> cartService.getCart(99L));
    }

    @Test
    @DisplayName("Debe agregar item al carrito exitosamente")
    void addItemToCart_Success() {
        CartItemRequestDTO request = new CartItemRequestDTO(1L, 2);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(cartRepository.findCartItemByUserIdAndProductId(1L, 1L)).thenReturn(Optional.empty());
        when(cartRepository.saveCartItem(any(CartItem.class))).thenReturn(testCartItem);
        when(cartRepository.getCartByUserId(1L)).thenReturn(testCart);

        CartResponseDTO result = cartService.addItemToCart(1L, request);

        assertNotNull(result);
        verify(cartRepository, times(1)).saveCartItem(any(CartItem.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando producto no existe al agregar al carrito")
    void addItemToCart_ProductNotFound() {
        CartItemRequestDTO request = new CartItemRequestDTO(99L, 2);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class,
                () -> cartService.addItemToCart(1L, request));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando stock insuficiente al agregar al carrito")
    void addItemToCart_InsufficientStock() {
        CartItemRequestDTO request = new CartItemRequestDTO(1L, 100);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        assertThrows(InsufficientStockException.class,
                () -> cartService.addItemToCart(1L, request));
    }

    @Test
    @DisplayName("Debe actualizar cantidad de item existente en el carrito")
    void addItemToCart_UpdateExistingItem() {
        CartItemRequestDTO request = new CartItemRequestDTO(1L, 3);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(cartRepository.findCartItemByUserIdAndProductId(1L, 1L)).thenReturn(Optional.of(testCartItem));
        when(cartRepository.getCartByUserId(1L)).thenReturn(testCart);

        CartResponseDTO result = cartService.addItemToCart(1L, request);

        assertNotNull(result);
        verify(cartRepository, times(1)).updateCartItemQuantity(1L, 5);
    }

    @Test
    @DisplayName("Debe actualizar item del carrito exitosamente")
    void updateCartItem_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(cartRepository.findCartItemByUserIdAndProductId(1L, 1L)).thenReturn(Optional.of(testCartItem));
        doNothing().when(cartRepository).updateCartItemQuantity(1L, 5);
        when(cartRepository.getCartByUserId(1L)).thenReturn(testCart);

        CartResponseDTO result = cartService.updateCartItem(1L, 1L, 5);

        assertNotNull(result);
        verify(cartRepository, times(1)).updateCartItemQuantity(1L, 5);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando item no encontrado al actualizar")
    void updateCartItem_ItemNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(cartRepository.findCartItemByUserIdAndProductId(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class,
                () -> cartService.updateCartItem(1L, 1L, 5));
    }

    @Test
    @DisplayName("Debe eliminar item del carrito exitosamente")
    void removeItemFromCart_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(cartRepository.findCartItemByUserIdAndProductId(1L, 1L)).thenReturn(Optional.of(testCartItem));
        doNothing().when(cartRepository).deleteCartItem(1L);
        when(cartRepository.getCartByUserId(1L)).thenReturn(Cart.builder().userId(1L).items(new ArrayList<>()).build());

        CartResponseDTO result = cartService.removeItemFromCart(1L, 1L);

        assertNotNull(result);
        verify(cartRepository, times(1)).deleteCartItem(1L);
    }

    @Test
    @DisplayName("Debe hacer checkout exitosamente y crear orden")
    void checkout_Success() {
        Order order = Order.builder()
                .id(1L)
                .userId(1L)
                .total(new BigDecimal("200.00"))
                .status(OrderStatus.PENDIENTE)
                .items(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(cartRepository.getCartByUserId(1L)).thenReturn(testCart);
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        doNothing().when(cartRepository).deleteAllByUserId(1L);

        OrderResponseDTO result = cartService.checkout(1L);

        assertNotNull(result);
        assertEquals(OrderStatus.PENDIENTE, result.status());
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(cartRepository, times(1)).deleteAllByUserId(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando carrito vacío en checkout")
    void checkout_EmptyCart() {
        Cart emptyCart = Cart.builder()
                .userId(1L)
                .items(new ArrayList<>())
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(cartRepository.getCartByUserId(1L)).thenReturn(emptyCart);

        assertThrows(BusinessException.class, () -> cartService.checkout(1L));
    }

    @Test
    @DisplayName("Debe limpiar el carrito")
    void clearCart_Success() {
        doNothing().when(cartRepository).deleteAllByUserId(1L);

        assertDoesNotThrow(() -> cartService.clearCart(1L));
        verify(cartRepository, times(1)).deleteAllByUserId(1L);
    }
}
