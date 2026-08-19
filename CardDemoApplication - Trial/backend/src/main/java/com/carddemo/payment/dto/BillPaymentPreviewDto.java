package com.carddemo.payment.dto;

import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;

@Getter @Builder
public class BillPaymentPreviewDto {
    private final String accountId;
    private final BigDecimal currentBalance;
    private final boolean payable;
}
