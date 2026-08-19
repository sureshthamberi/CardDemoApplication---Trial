package com.carddemo.navigation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** Request body for validating a menu option. */
@Data
public class ValidateOptionRequest {

    @NotBlank(message = "Option is required")
    @Size(max = 8, message = "Option must be at most 8 characters")
    private String option;
}
