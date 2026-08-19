package com.carddemo.account.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/** PUT /api/v1/accounts/{accountId} request body. LLD 4.6.2 */
@Data
public class UpdateAccountRequest {

    @Valid
    private AccountUpdateFields account;

    @Valid
    private CustomerUpdateFields customer;

    @NotNull(message = "Account row version is required")
    private Long accountRowVersion;

    @NotNull(message = "Customer row version is required")
    private Long customerRowVersion;

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AccountUpdateFields {
        @Size(max = 30) private String     accountStatus;
        @DecimalMin("0") private BigDecimal creditLimit;
        @DecimalMin("0") private BigDecimal cashCreditLimit;
        private LocalDate expirationDate;
        private LocalDate reissueDate;
        @Size(max = 64)  private String groupId;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CustomerUpdateFields {
        @NotBlank @Size(max = 100) private String  firstName;
        @Size(max = 100)           private String  middleName;
        @NotBlank @Size(max = 100) private String  lastName;
        @Size(max = 255)           private String  addressLine1;
        @Size(max = 255)           private String  addressLine2;
        @Size(max = 100)           private String  city;
        @Size(max = 50)            private String  state;
        @Size(max = 20)            private String  zip;
        @Size(max = 100)           private String  country;
        @Size(max = 20)            private String  phone1;
        @Size(max = 20)            private String  phone2;
        @Min(0) @Max(999)          private Integer creditScore;
        private LocalDate                          dateOfBirth;
        @Size(max = 50)            private String  governmentId;
        @Size(max = 64)            private String  electronicFundsAccountRef;
        @Pattern(regexp = "[YN]")  private String  primaryCardHolderIndicator;
    }
}
