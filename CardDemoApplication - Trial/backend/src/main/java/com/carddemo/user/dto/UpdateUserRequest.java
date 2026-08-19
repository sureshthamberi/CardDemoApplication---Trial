package com.carddemo.user.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

/** Request body for updating a user. LLD 4.5.4 */
@Data
public class UpdateUserRequest {

    @NotBlank(message = "First Name is required")
    @Size(min = 1, max = 100)
    private String firstName;

    @NotBlank(message = "Last Name is required")
    @Size(min = 1, max = 100)
    private String lastName;

    @NotBlank(message = "Password is required")
    @Size(min = 1, max = 256)
    private String password;

    @NotNull(message = "User Type is required")
    @Pattern(regexp = "ADMIN|STANDARD", message = "User Type must be ADMIN or STANDARD")
    private String userType;

    @NotNull(message = "Row version is required")
    @Min(value = 0, message = "Row version must be non-negative")
    private Long rowVersion;
}
