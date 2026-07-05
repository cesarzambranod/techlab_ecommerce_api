package com.techlab.integration;

import com.techlab.application.ports.in.UserUseCasePorts;
import com.techlab.domain.enums.UserRole;
import com.techlab.shared.dto.request.AuthRequestDTO;
import com.techlab.shared.dto.request.UserRequestDTO;
import com.techlab.shared.dto.response.AuthResponseDTO;
import com.techlab.shared.dto.response.UserResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Tests de integración para UserController")
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserUseCasePorts userUseCase;

    @Test
    @DisplayName("POST /api/users/register debe registrar un usuario")
    void registerUser_ReturnsCreatedUser() throws Exception {
        UserRequestDTO request = new UserRequestDTO(
                "newuser",
                "new@example.com",
                "Password123!",
                "USER"
        );

        UserResponseDTO response = new UserResponseDTO(
                1L,
                "newuser",
                "new@example.com",
                UserRole.USER,
                LocalDateTime.now()
        );

        when(userUseCase.registerUser(any(UserRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "newuser",
                                    "email": "new@example.com",
                                    "password": "Password123!",
                                    "role": "USER"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.email").value("new@example.com"));
    }

    @Test
    @DisplayName("POST /api/users/login debe autenticar usuario")
    void login_ReturnsAuthResponse() throws Exception {
        AuthRequestDTO request = new AuthRequestDTO("testuser", "Password123!");

        UserResponseDTO userResponse = new UserResponseDTO(
                1L,
                "testuser",
                "test@example.com",
                UserRole.USER,
                LocalDateTime.now()
        );

        AuthResponseDTO authResponse = new AuthResponseDTO(
                "jwt-token-123",
                1L,
                "testuser",
                "USER"
        );

        when(userUseCase.authenticateUser("testuser", "Password123!")).thenReturn("jwt-token-123");
        when(userUseCase.getUserByUsername("testuser")).thenReturn(userResponse);

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "testuser",
                                    "password": "Password123!"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token-123"))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @DisplayName("GET /api/users debe listar todos los usuarios")
    void getAllUsers_ReturnsUserList() throws Exception {
        List<UserResponseDTO> users = Arrays.asList(
                new UserResponseDTO(1L, "user1", "user1@example.com", UserRole.USER, LocalDateTime.now()),
                new UserResponseDTO(2L, "user2", "user2@example.com", UserRole.USER, LocalDateTime.now())
        );

        when(userUseCase.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].username").value("user1"))
                .andExpect(jsonPath("$[1].username").value("user2"));
    }

    @Test
    @DisplayName("GET /api/users/{id} debe retornar usuario por ID")
    void getUserById_ReturnsUser() throws Exception {
        UserResponseDTO user = new UserResponseDTO(
                1L,
                "testuser",
                "test@example.com",
                UserRole.USER,
                LocalDateTime.now()
        );

        when(userUseCase.getUserById(1L)).thenReturn(user);

        mockMvc.perform(get("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }
}
