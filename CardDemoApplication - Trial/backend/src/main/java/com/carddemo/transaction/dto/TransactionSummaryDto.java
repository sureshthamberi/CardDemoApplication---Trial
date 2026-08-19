package com.carddemo.transaction.dto;

import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Builder
public class TransactionSummaryDto {
    private final String transactionId;
    private final LocalDateTime date;
    private final String description;
    private final BigDecimal amount;
    private final String transactionType;
}
