package com.techlab.application.services;

import com.techlab.application.ports.in.UserUseCasePorts;
import com.techlab.application.ports.out.UserRepositoryPort;
import com.techlab.domain.enums.UserRole;
import com.techlab.domain.exceptions.BusinessException;
import com.techlab.domain.exceptions.UserNotFoundException;
import com.techlab.domain.model.User;
import com.techlab.shared.dto.request.UserRequestDTO;
import com.techlab.shared.dto.response.UserResponseDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio que implementa los casos de uso de usuarios.
 */
@Service
@Transactional
public class UserService implements UserUseCasePorts {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(UserRepositoryPort userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public UserResponseDTO registerUser(UserRequestDTO request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new BusinessException("El nombre de usuario ya existe");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException("El email ya está registrado");
        }

        UserRole role = UserRole.USER;
        if (request.role() != null && !request.role().isEmpty()) {
            try {
                role = UserRole.valueOf(request.role().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Rol inválido: " + request.role());
            }
        }

        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(role)
                .createdAt(LocalDateTime.now())
                .build();

        User saved = userRepository.save(user);
        return mapToResponseDTO(saved);
    }

    @Override
    public String authenticateUser(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Credenciales inválidas"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UserNotFoundException("Credenciales inválidas");
        }

        return jwtService.generateToken(user);
    }

    @Override
    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return mapToResponseDTO(user);
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado: " + username));
        return mapToResponseDTO(user);
    }

    private UserResponseDTO mapToResponseDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}
