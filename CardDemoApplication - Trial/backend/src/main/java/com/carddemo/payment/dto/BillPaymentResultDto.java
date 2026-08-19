package com.carddemo.payment.dto;

import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;

@Getter @Builder
public class BillPaymentResultDto {
    private final String accountId;
    private final BigDecimal paymentAmount;
    private final String transactionId;
    private final BigDecimal remainingBalance;
}
