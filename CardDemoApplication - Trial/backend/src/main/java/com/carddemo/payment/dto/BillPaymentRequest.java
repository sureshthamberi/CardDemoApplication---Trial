package com.carddemo.payment.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class BillPaymentRequest {
    @NotBlank @Pattern(regexp = "[1-9][0-9]{10}", message = "Account must be non-zero 11-digit number")
    private String accountId;

    @NotNull @Pattern(regexp = "Y", message = "Confirmation must be Y")
    private String confirmation;
}
