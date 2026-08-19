package com.carddemo.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** Login request payload. */
@Data
public class LoginRequest {

    @NotBlank(message = "User ID is required")
    @Size(max = 64, message = "User ID must be at most 64 characters")
    private String userId;

    @NotBlank(message = "Password is required")
    @Size(max = 256, message = "Password must be at most 256 characters")
    private String password;
}
