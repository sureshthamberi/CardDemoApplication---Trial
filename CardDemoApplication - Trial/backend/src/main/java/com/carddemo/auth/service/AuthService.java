package com.carddemo.auth.service;

import com.carddemo.auth.dto.LoginRequest;
import com.carddemo.auth.dto.LoginResponse;
import com.carddemo.auth.dto.UserContextResponse;
import com.carddemo.auth.entity.UserEntity;
import com.carddemo.auth.repository.UserRepository;
import com.carddemo.common.exception.AuthenticationException;
import com.carddemo.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Handles login, logout and current-user context.
 * Follows the business logic defined in LLD Section 4.3.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    // Permissions mapped from userType (LLD 4.1.5)
    private static final Map<String, List<String>> ROLE_PERMISSIONS = Map.of(
            "ADMIN",    List.of("USER_ADMIN", "ACCOUNT_VIEW", "ACCOUNT_UPDATE",
                                "CARD_VIEW", "CARD_UPDATE", "TRANSACTION_VIEW",
                                "TRANSACTION_ADD", "TXN_TYPE_ADMIN", "FRAUD_ACTION",
                                "REPORT_REQUEST", "PENDING_AUTH_VIEW", "BILL_PAYMENT"),
            "STANDARD", List.of("ACCOUNT_VIEW", "ACCOUNT_UPDATE", "CARD_VIEW",
                                "CARD_UPDATE", "TRANSACTION_VIEW", "TRANSACTION_ADD",
                                "FRAUD_ACTION", "REPORT_REQUEST", "PENDING_AUTH_VIEW",
                                "BILL_PAYMENT")
    );

    private static final Map<String, String> LANDING_PAGES = Map.of(
            "ADMIN",    "ADMIN_MENU",
            "STANDARD", "MAIN_MENU"
    );

    private final UserRepository  userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    /**
     * Authenticate a user and return a JWT-based login response.
     * LLD 4.3.1
     */
    public LoginResponse login(LoginRequest request) {
        String userId   = request.getUserId().trim();
        String password = request.getPassword().trim();

        if (userId.isEmpty()) {
            throw new AuthenticationException("AUTH-VAL-001", "User ID is required");
        }
        if (password.isEmpty()) {
            throw new AuthenticationException("AUTH-VAL-001", "Password is required");
        }

        UserEntity user = userRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.warn("Login attempt — user not found: {}", userId);
                    return new AuthenticationException("AUTH-401-001", "User not found");
                });

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            log.warn("Login attempt — wrong password for user: {}", userId);
            throw new AuthenticationException("AUTH-401-002", "Wrong password");
        }

        String token      = tokenProvider.generateToken(user.getUserId(), user.getUserType());
        Date   expiryDate = tokenProvider.getExpiry(token);
        String landingPage = LANDING_PAGES.getOrDefault(user.getUserType(), "MAIN_MENU");

        log.info("Successful login for user: {} type: {}", userId, user.getUserType());

        return LoginResponse.builder()
                .userId(user.getUserId())
                .displayName(user.getFirstName() + " " + user.getLastName())
                .userType(user.getUserType())
                .landingPage(landingPage)
                .token(token)
                .expiresAt(expiryDate.toInstant().toString())
                .build();
    }

    /**
     * Retrieve current user context from an authenticated principal.
     * LLD 4.3.3
     */
    public UserContextResponse getCurrentUser(String userId) {
        UserEntity user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new AuthenticationException("AUTH-401", "User context not found"));

        List<String> permissions = ROLE_PERMISSIONS.getOrDefault(user.getUserType(), List.of());

        return UserContextResponse.builder()
                .userId(user.getUserId())
                .displayName(user.getFirstName() + " " + user.getLastName())
                .userType(user.getUserType())
                .permissions(permissions)
                .build();
    }
}
