package com.carddemo.auth.controller;

import com.carddemo.auth.dto.LoginRequest;
import com.carddemo.auth.dto.LoginResponse;
import com.carddemo.auth.dto.UserContextResponse;
import com.carddemo.auth.service.AuthService;
import com.carddemo.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication REST endpoints.
 * LLD Section 4.3 — Login, Logout, Current User.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "User authentication and session management")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * POST /api/v1/auth/login
     * Authenticate a user with User ID and Password.
     */
    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticate user and return JWT token with landing page")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request for userId: {}", request.getUserId());
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok("Login successful", response));
    }

    /**
     * POST /api/v1/auth/logout
     * Invalidate the current session/token (stateless — client discards token).
     */
    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Invalidate current session")
    public ResponseEntity<ApiResponse<Void>> logout(@AuthenticationPrincipal String userId) {
        log.info("Logout for userId: {}", userId);
        // Stateless JWT: client discards token. Server-side blacklist can be added if required.
        return ResponseEntity.ok(ApiResponse.ok("Logout successful", null));
    }

    /**
     * GET /api/v1/auth/me
     * Return current authenticated user context.
     */
    @GetMapping("/me")
    @Operation(summary = "Current User", description = "Return authenticated user profile and permissions")
    public ResponseEntity<ApiResponse<UserContextResponse>> me(@AuthenticationPrincipal String userId) {
        UserContextResponse response = authService.getCurrentUser(userId);
        return ResponseEntity.ok(ApiResponse.ok("User context retrieved", response));
    }
}
