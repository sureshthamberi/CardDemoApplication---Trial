package com.carddemo.pendingauth.dto;

import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;

@Getter @Builder
public class PendingAuthItemDto {
    private final String authorizationId;
    private final BigDecimal amount;
    private final String merchantName;
    private final String status;
    private final String fraudFlag;
}
