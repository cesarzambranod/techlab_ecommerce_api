package com.techlab.unit;

import com.techlab.application.ports.out.UserRepositoryPort;
import com.techlab.application.services.JwtService;
import com.techlab.application.services.UserService;
import com.techlab.domain.enums.UserRole;
import com.techlab.domain.exceptions.BusinessException;
import com.techlab.domain.exceptions.UserNotFoundException;
import com.techlab.domain.model.User;
import com.techlab.shared.dto.request.UserRequestDTO;
import com.techlab.shared.dto.response.UserResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitarios para UserService")
class UserServiceTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserRequestDTO testRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .role(UserRole.USER)
                .createdAt(LocalDateTime.now())
                .build();

        testRequest = new UserRequestDTO(
                "testuser",
                "test@example.com",
                "Password123!",
                "USER"
        );
    }

    @Test
    @DisplayName("Debe registrar un usuario exitosamente")
    void registerUser_Success() {
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Password123!")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserResponseDTO result = userService.registerUser(testRequest);

        assertNotNull(result);
        assertEquals("testuser", result.username());
        assertEquals("test@example.com", result.email());
        assertEquals(UserRole.USER, result.role());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el username ya existe")
    void registerUser_UsernameExists() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        assertThrows(BusinessException.class, () -> userService.registerUser(testRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el email ya existe")
    void registerUser_EmailExists() {
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThrows(BusinessException.class, () -> userService.registerUser(testRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Debe registrar un usuario con rol ADMIN cuando se especifica")
    void registerUser_AdminRole() {
        UserRequestDTO adminRequest = new UserRequestDTO(
                "adminuser",
                "admin@example.com",
                "AdminPass123!",
                "ADMIN"
        );

        User adminUser = User.builder()
                .id(2L)
                .username("adminuser")
                .email("admin@example.com")
                .password("encodedPassword")
                .role(UserRole.ADMIN)
                .createdAt(LocalDateTime.now())
                .build();

        when(userRepository.existsByUsername("adminuser")).thenReturn(false);
        when(userRepository.existsByEmail("admin@example.com")).thenReturn(false);
        when(passwordEncoder.encode("AdminPass123!")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(adminUser);

        UserResponseDTO result = userService.registerUser(adminRequest);

        assertNotNull(result);
        assertEquals(UserRole.ADMIN, result.role());
    }

    @Test
    @DisplayName("Debe autenticar usuario exitosamente")
    void authenticateUser_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("Password123!", "encodedPassword")).thenReturn(true);
        when(jwtService.generateToken(testUser)).thenReturn("jwt-token");

        String token = userService.authenticateUser("testuser", "Password123!");

        assertNotNull(token);
        assertEquals("jwt-token", token);
    }

    @Test
    @DisplayName("Debe lanzar excepción con credenciales inválidas - usuario no existe")
    void authenticateUser_UserNotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.authenticateUser("nonexistent", "password"));
    }

    @Test
    @DisplayName("Debe lanzar excepción con credenciales inválidas - password incorrecto")
    void authenticateUser_InvalidPassword() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

        assertThrows(UserNotFoundException.class,
                () -> userService.authenticateUser("testuser", "wrongpassword"));
    }

    @Test
    @DisplayName("Debe obtener usuario por ID")
    void getUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        UserResponseDTO result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("testuser", result.username());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando usuario no existe por ID")
    void getUserById_NotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(99L));
    }

    @Test
    @DisplayName("Debe listar todos los usuarios")
    void getAllUsers_Success() {
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findAll()).thenReturn(users);

        List<UserResponseDTO> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).username());
    }

    @Test
    @DisplayName("Debe obtener usuario por username")
    void getUserByUsername_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        UserResponseDTO result = userService.getUserByUsername("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.username());
        assertEquals("test@example.com", result.email());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando usuario no existe por username")
    void getUserByUsername_NotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.getUserByUsername("nonexistent"));
    }
}
