package com.carddemo.user.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

/** Request body for creating a user. LLD 4.5.3 */
@Data
public class CreateUserRequest {

    @NotBlank(message = "First Name is required")
    @Size(min = 1, max = 100, message = "First Name must be between 1 and 100 characters")
    private String firstName;

    @NotBlank(message = "Last Name is required")
    @Size(min = 1, max = 100, message = "Last Name must be between 1 and 100 characters")
    private String lastName;

    @NotBlank(message = "User ID is required")
    @Size(min = 1, max = 64, message = "User ID must be between 1 and 64 characters")
    private String userId;

    @NotBlank(message = "Password is required")
    @Size(min = 1, max = 256, message = "Password must be between 1 and 256 characters")
    private String password;

    @NotNull(message = "User Type is required")
    @Pattern(regexp = "ADMIN|STANDARD", message = "User Type must be ADMIN or STANDARD")
    private String userType;
}
