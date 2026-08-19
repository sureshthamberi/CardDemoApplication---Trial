package com.carddemo.internal.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AuthDecisionRequest {
    @NotBlank @Pattern(regexp = "[1-9][0-9]{10}")
    private String accountId;

    @NotBlank @Pattern(regexp = "[0-9]{16}")
    private String cardNumber;

    @NotNull @DecimalMin("0.01")
    private BigDecimal amount;

    @Size(max = 64)  private String merchantId;
    @Size(max = 255) private String merchantName;
    @Size(max = 100) private String merchantCity;
    @Size(max = 20)  private String merchantZip;

    private LocalDateTime requestTimestamp;
}
