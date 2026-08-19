package com.carddemo.navigation.controller;

import com.carddemo.common.dto.ApiResponse;
import com.carddemo.navigation.dto.MenuResponse;
import com.carddemo.navigation.dto.ValidateOptionRequest;
import com.carddemo.navigation.dto.ValidateOptionResponse;
import com.carddemo.navigation.service.NavigationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

/**
 * Navigation REST endpoints.
 * LLD Section 4.4 — Main Menu, Admin Menu, Validate Option.
 */
@RestController
@RequestMapping("/api/v1/navigation")
@Tag(name = "Navigation", description = "Menu navigation and option validation")
@RequiredArgsConstructor
public class NavigationController {

    private final NavigationService navigationService;

    /** GET /api/v1/navigation/main-menu */
    @GetMapping("/main-menu")
    @Operation(summary = "Get Main Menu", description = "Return menu options for standard user")
    public ResponseEntity<ApiResponse<MenuResponse>> getMainMenu() {
        return ResponseEntity.ok(ApiResponse.ok("Menu retrieved", navigationService.getMainMenu()));
    }

    /** GET /api/v1/navigation/admin-menu */
    @GetMapping("/admin-menu")
    @Operation(summary = "Get Admin Menu", description = "Return admin menu options")
    public ResponseEntity<ApiResponse<MenuResponse>> getAdminMenu() {
        return ResponseEntity.ok(ApiResponse.ok("Menu retrieved", navigationService.getAdminMenu()));
    }

    /** POST /api/v1/navigation/validate-option */
    @PostMapping("/validate-option")
    @Operation(summary = "Validate Menu Option", description = "Validate selected menu option and return target page")
    public ResponseEntity<ApiResponse<ValidateOptionResponse>> validateOption(
            @Valid @RequestBody ValidateOptionRequest request) {

        String userType = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a.startsWith("ROLE_"))
                .map(a -> a.substring(5))
                .findFirst()
                .orElse("STANDARD");

        ValidateOptionResponse response = navigationService.validateOption(request.getOption(), userType);
        return ResponseEntity.ok(ApiResponse.ok("Option validated", response));
    }
}
