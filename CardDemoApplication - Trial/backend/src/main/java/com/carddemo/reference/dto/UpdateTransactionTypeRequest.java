package com.carddemo.reference.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UpdateTransactionTypeRequest {
    @NotBlank @Size(min = 1, max = 255)
    private String description;

    @NotNull @Min(0)
    private Long rowVersion;
}
