package com.carddemo.transaction.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateTransactionRequest {
    @Pattern(regexp = "[1-9][0-9]{10}", message = "Account must be non-zero 11-digit number")
    private String accountId;

    @Pattern(regexp = "[0-9]{16}", message = "Card number must be 16 digits")
    private String cardNumber;

    @NotBlank private String transactionType;
    @NotBlank private String categoryType;
    @NotBlank private String source;

    @NotNull @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private BigDecimal amount;

    @NotBlank @Size(max = 255) private String description;

    @NotNull private LocalDate originalDate;
    @NotNull private LocalDate processDate;

    @Size(max = 64)  private String merchantId;
    @Size(max = 255) private String merchantName;
    @Size(max = 100) private String merchantCity;
    @Size(max = 20)  private String merchantZip;

    @NotNull @Pattern(regexp = "Y", message = "Confirmation must be Y")
    private String confirmation;
}
