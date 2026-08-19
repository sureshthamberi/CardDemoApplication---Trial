package com.carddemo.card.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UpdateCardRequest {
    @NotBlank @Pattern(regexp = "[1-9][0-9]{10}", message = "Account must be non-zero 11-digit number")
    private String accountId;

    @NotBlank @Pattern(regexp = "^[A-Za-z ]+$", message = "Card name can only contain alphabets and spaces")
    @Size(min = 1, max = 100)
    private String cardName;

    @NotNull @Pattern(regexp = "[YN]", message = "Active status must be Y or N")
    private String activeStatus;

    @NotNull @Min(1) @Max(12)
    private Integer expiryMonth;

    @NotNull @Min(1900) @Max(9999)
    private Integer expiryYear;

    @NotNull @Min(0)
    private Long rowVersion;
}
