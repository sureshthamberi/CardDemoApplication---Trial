package com.carddemo.auth.service;

import com.carddemo.auth.dto.LoginRequest;
import com.carddemo.auth.dto.LoginResponse;
import com.carddemo.auth.entity.UserEntity;
import com.carddemo.auth.repository.UserRepository;
import com.carddemo.common.exception.AuthenticationException;
import com.carddemo.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Unit Tests")
class AuthServiceTest {

    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtTokenProvider tokenProvider;
    @InjectMocks AuthService authService;

    private UserEntity adminUser;

    @BeforeEach
    void setUp() {
        adminUser = UserEntity.builder()
                .userId("USR001")
                .firstName("John")
                .lastName("Doe")
                .passwordHash("$2a$10$hashedPassword")
                .userType("ADMIN")
                .status("ACTIVE")
                .version(0L)
                .build();
    }

    @Test
    @DisplayName("Login success — ADMIN user returns ADMIN_MENU landing page")
    void loginSuccess_AdminUser_ReturnsAdminLandingPage() {
        LoginRequest request = new LoginRequest();
        request.setUserId("USR001");
        request.setPassword("Admin@123");

        when(userRepository.findByUserId("USR001")).thenReturn(Optional.of(adminUser));
        when(passwordEncoder.matches("Admin@123", adminUser.getPasswordHash())).thenReturn(true);
        when(tokenProvider.generateToken("USR001", "ADMIN")).thenReturn("jwt-token");
        when(tokenProvider.getExpiry("jwt-token")).thenReturn(new Date());

        LoginResponse response = authService.login(request);

        assertThat(response.getUserId()).isEqualTo("USR001");
        assertThat(response.getUserType()).isEqualTo("ADMIN");
        assertThat(response.getLandingPage()).isEqualTo("ADMIN_MENU");
        assertThat(response.getToken()).isEqualTo("jwt-token");
    }

    @Test
    @DisplayName("Login failure — user not found throws AuthenticationException")
    void loginFailure_UserNotFound_ThrowsAuthenticationException() {
        LoginRequest request = new LoginRequest();
        request.setUserId("UNKNOWN");
        request.setPassword("password");

        when(userRepository.findByUserId("UNKNOWN")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    @DisplayName("Login failure — wrong password throws AuthenticationException")
    void loginFailure_WrongPassword_ThrowsAuthenticationException() {
        LoginRequest request = new LoginRequest();
        request.setUserId("USR001");
        request.setPassword("WrongPassword");

        when(userRepository.findByUserId("USR001")).thenReturn(Optional.of(adminUser));
        when(passwordEncoder.matches("WrongPassword", adminUser.getPasswordHash())).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("Wrong password");
    }

    @Test
    @DisplayName("Login — blank userId after trim throws AuthenticationException")
    void loginFailure_BlankUserId_ThrowsAuthenticationException() {
        LoginRequest request = new LoginRequest();
        request.setUserId("   ");
        request.setPassword("password");

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(AuthenticationException.class);
    }
}
