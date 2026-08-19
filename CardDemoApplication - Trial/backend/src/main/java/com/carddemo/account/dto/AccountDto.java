package com.carddemo.account.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Account section of composite account response. LLD 4.6.1 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountDto {
    private final String     accountId;
    private final String     accountStatus;
    private final BigDecimal currentBalance;
    private final BigDecimal creditLimit;
    private final BigDecimal cashCreditLimit;
    private final BigDecimal currentCycleCredit;
    private final BigDecimal currentCycleDebit;
    private final LocalDate  openDate;
    private final LocalDate  expirationDate;
    private final LocalDate  reissueDate;
    private final String     groupId;
    private final Long       rowVersion;
}
