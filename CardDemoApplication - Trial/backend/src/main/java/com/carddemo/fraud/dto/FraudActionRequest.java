package com.carddemo.fraud.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FraudActionRequest {
    @Size(max = 500, message = "Notes must be at most 500 characters")
    private String notes;
}
