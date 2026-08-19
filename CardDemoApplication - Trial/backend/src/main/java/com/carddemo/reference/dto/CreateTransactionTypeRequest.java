package com.carddemo.reference.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateTransactionTypeRequest {
    @NotBlank @Size(min = 1, max = 50)
    private String typeCode;

    @NotBlank @Size(min = 1, max = 255)
    private String description;
}
