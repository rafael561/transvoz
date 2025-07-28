package com.transvoz.module.user;

import com.transvoz.module.user.dto.RegisterRequest;
import com.transvoz.module.user.dto.UserResponse;
import com.transvoz.module.user.entity.User;
import com.transvoz.module.user.repository.UserRepository;
import com.transvoz.module.user.service.UserService;
import com.transvoz.shared.enums.Role;
import com.transvoz.shared.exception.TransVozException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private RegisterRequest registerRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");

        user = User.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .password("encodedPassword")
                .firstName("John")
                .lastName("Doe")
                .role(Role.USER)
                .isActive(true)
                .build();
    }

    @Test
    void registerUser_ShouldReturnUserResponse_WhenValidRequest() {
        // Given
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        UserResponse result = userService.registerUser(registerRequest);

        // Then
        assertNotNull(result);
        assertEquals(registerRequest.getEmail(), result.getEmail());
        assertEquals(registerRequest.getFirstName(), result.getFirstName());
        assertEquals(registerRequest.getLastName(), result.getLastName());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_ShouldThrowException_WhenEmailExists() {
        // Given
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        // When & Then
        assertThrows(TransVozException.class, () -> userService.registerUser(registerRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getCurrentUser_ShouldReturnUserResponse_WhenUserExists() {
        // Given
        when(userRepository.findActiveByEmail(user.getEmail())).thenReturn(Optional.of(user));

        // When
        UserResponse result = userService.getCurrentUser(user.getEmail());

        // Then
        assertNotNull(result);
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getFirstName(), result.getFirstName());
    }
}
