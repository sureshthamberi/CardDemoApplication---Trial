package com.carddemo.pendingauth.dto;

import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Builder
public class PendingAuthDetailDto {
    private final String authorizationId;
    private final String accountId;
    private final String cardNumberMasked;
    private final BigDecimal amount;
    private final String merchantId;
    private final String merchantName;
    private final String merchantCity;
    private final String merchantZip;
    private final String status;
    private final String fraudFlag;
    private final LocalDateTime requestTimestamp;
}
