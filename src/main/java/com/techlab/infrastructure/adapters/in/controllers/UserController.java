package com.techlab.infrastructure.adapters.in.controllers;

import com.techlab.application.ports.in.UserUseCasePorts;
import com.techlab.shared.dto.request.AuthRequestDTO;
import com.techlab.shared.dto.request.UserRequestDTO;
import com.techlab.shared.dto.response.AuthResponseDTO;
import com.techlab.shared.dto.response.UserResponseDTO;
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
 * Controlador REST para la gestión de usuarios.
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "Usuarios", description = "Operaciones de gestión de usuarios y autenticación")
public class UserController {

    private final UserUseCasePorts userUseCase;

    public UserController(UserUseCasePorts userUseCase) {
        this.userUseCase = userUseCase;
    }

    @Operation(summary = "Registrar nuevo usuario", description = "Registra un nuevo usuario en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o usuario ya existe"),
            @ApiResponse(responseCode = "409", description = "Conflicto - usuario o email ya existen")
    })
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(
            @Valid @RequestBody UserRequestDTO request) {
        UserResponseDTO created = userUseCase.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Autenticar usuario", description = "Autentica un usuario y retorna un token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autenticación exitosa"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(
            @Valid @RequestBody AuthRequestDTO request) {
        String token = userUseCase.authenticateUser(request.username(), request.password());
        UserResponseDTO user = userUseCase.getUserByUsername(request.username());

        AuthResponseDTO response = new AuthResponseDTO(
                token,
                user.id(),
                user.username(),
                user.role().name()
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Listar todos los usuarios", description = "Obtiene una lista de todos los usuarios (ADMIN)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente")
    })
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = userUseCase.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Obtener usuario por ID", description = "Obtiene los detalles de un usuario específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(
            @Parameter(description = "ID del usuario") @PathVariable Long id) {
        UserResponseDTO user = userUseCase.getUserById(id);
        return ResponseEntity.ok(user);
    }
}
