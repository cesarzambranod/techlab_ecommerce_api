package com.techlab.infrastructure.config;

import com.techlab.domain.enums.UserRole;
import com.techlab.infrastructure.adapters.out.entities.ProductEntity;
import com.techlab.infrastructure.adapters.out.entities.UserEntity;
import com.techlab.infrastructure.adapters.out.repositories.SpringDataProductRepository;
import com.techlab.infrastructure.adapters.out.repositories.SpringDataUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Configuración para inicializar datos de prueba en la base de datos.
 */
@Configuration
public class DataInitializer {

    @Bean
    @Profile("!test")
    public CommandLineRunner initData(
            SpringDataProductRepository productRepository,
            SpringDataUserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {
            if (productRepository.count() == 0) {
                initializeProducts(productRepository);
            }

            if (userRepository.count() == 0) {
                initializeUsers(userRepository, passwordEncoder);
            }
        };
    }

    private void initializeProducts(SpringDataProductRepository productRepository) {
        List<ProductEntity> products = Arrays.asList(
                ProductEntity.builder()
                        .name("Laptop Dell XPS 15")
                        .description("Laptop de alta gama con procesador Intel Core i7, 16GB RAM, 512GB SSD")
                        .price(new BigDecimal("1200.00"))
                        .category("Electrónica")
                        .imageUrl("https://example.com/images/laptop-dell.jpg")
                        .stock(10)
                        .deleted(false)
                        .build(),

                ProductEntity.builder()
                        .name("Mouse Logitech MX Master 3")
                        .description("Mouse inalámbrico de precisión con scroll electromagnético")
                        .price(new BigDecimal("45.50"))
                        .category("Accesorios")
                        .imageUrl("https://example.com/images/mouse-logitech.jpg")
                        .stock(25)
                        .deleted(false)
                        .build(),

                ProductEntity.builder()
                        .name("Teclado Mecánico Corsair K70")
                        .description("Teclado mecánico con switches Cherry MX y retroiluminación RGB")
                        .price(new BigDecimal("85.00"))
                        .category("Accesorios")
                        .imageUrl("https://example.com/images/teclado-corsair.jpg")
                        .stock(15)
                        .deleted(false)
                        .build(),

                ProductEntity.builder()
                        .name("Monitor LG 27 inch 4K")
                        .description("Monitor 4K UHD de 27 pulgadas ideal para trabajo y gaming")
                        .price(new BigDecimal("350.00"))
                        .category("Electrónica")
                        .imageUrl("https://example.com/images/monitor-lg.jpg")
                        .stock(8)
                        .deleted(false)
                        .build(),

                ProductEntity.builder()
                        .name("Auriculares Sony WH-1000XM5")
                        .description("Auriculares inalámbricos con cancelación de ruido premium")
                        .price(new BigDecimal("280.00"))
                        .category("Audio")
                        .imageUrl("https://example.com/images/auriculares-sony.jpg")
                        .stock(12)
                        .deleted(false)
                        .build()
        );

        productRepository.saveAll(products);
        System.out.println("=== Datos de productos inicializados ===");
    }

    private void initializeUsers(SpringDataUserRepository userRepository, PasswordEncoder passwordEncoder) {
        List<UserEntity> users = Arrays.asList(
                UserEntity.builder()
                        .username("admin")
                        .email("admin@techlab.com")
                        .password(passwordEncoder.encode("Admin123!"))
                        .role(UserRole.ADMIN)
                        .createdAt(LocalDateTime.now())
                        .build(),

                UserEntity.builder()
                        .username("user")
                        .email("user@techlab.com")
                        .password(passwordEncoder.encode("User123!"))
                        .role(UserRole.USER)
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        userRepository.saveAll(users);
        System.out.println("=== Datos de usuarios inicializados ===");
    }
}
