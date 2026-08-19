package com.carddemo.transaction.dto;

import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Builder
public class TransactionDetailDto {
    private final String transactionId;
    private final String cardNumberMasked;
    private final String typeCode;
    private final String categoryCode;
    private final String source;
    private final BigDecimal amount;
    private final String description;
    private final LocalDateTime originalTimestamp;
    private final LocalDateTime processedTimestamp;
    private final String merchantId;
    private final String merchantName;
    private final String merchantCity;
    private final String merchantZip;
}
