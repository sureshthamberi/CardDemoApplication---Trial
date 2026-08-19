package com.carddemo.user.controller;

import com.carddemo.common.dto.ApiResponse;
import com.carddemo.common.dto.PageResponse;
import com.carddemo.user.dto.*;
import com.carddemo.user.service.UserAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * User administration REST endpoints.
 * LLD Section 4.5 — CRUD for users.
 * Requires ADMIN role.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/admin/users")
@Tag(name = "User Administration", description = "Admin-only user CRUD operations")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class UserAdminController {

    private final UserAdminService userAdminService;

    /** GET /api/v1/admin/users */
    @GetMapping
    @Operation(summary = "List Users", description = "Return paginated list of users")
    public ResponseEntity<ApiResponse<PageResponse<UserSummaryDto>>> listUsers(
            @RequestParam(defaultValue = "")  String startUserId,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int pageSize) {

        PageResponse<UserSummaryDto> data = userAdminService.listUsers(page, pageSize, startUserId);
        return ResponseEntity.ok(ApiResponse.ok("Users retrieved", data));
    }

    /** GET /api/v1/admin/users/{userId} */
    @GetMapping("/{userId}")
    @Operation(summary = "Get User", description = "Retrieve one user record")
    public ResponseEntity<ApiResponse<UserDetailDto>> getUser(@PathVariable String userId) {
        return ResponseEntity.ok(ApiResponse.ok("User retrieved", userAdminService.getUser(userId)));
    }

    /** POST /api/v1/admin/users */
    @PostMapping
    @Operation(summary = "Create User", description = "Create a new user")
    public ResponseEntity<ApiResponse<Map<String, String>>> createUser(
            @Valid @RequestBody CreateUserRequest request,
            @AuthenticationPrincipal String principal) {

        String createdUserId = userAdminService.createUser(request, principal);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("User created successfully", Map.of("userId", createdUserId)));
    }

    /** PUT /api/v1/admin/users/{userId} */
    @PutMapping("/{userId}")
    @Operation(summary = "Update User", description = "Update an existing user")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateUser(
            @PathVariable String userId,
            @Valid @RequestBody UpdateUserRequest request,
            @AuthenticationPrincipal String principal) {

        long newVersion = userAdminService.updateUser(userId, request, principal);
        return ResponseEntity.ok(ApiResponse.ok("User updated successfully",
                Map.of("userId", userId, "rowVersion", newVersion)));
    }

    /** DELETE /api/v1/admin/users/{userId} */
    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete User", description = "Delete an existing user")
    public ResponseEntity<ApiResponse<Map<String, String>>> deleteUser(
            @PathVariable String userId,
            @AuthenticationPrincipal String principal) {

        userAdminService.deleteUser(userId, principal);
        return ResponseEntity.ok(ApiResponse.ok("User deleted successfully", Map.of("userId", userId)));
    }
}
