package com.carddemo.fraud.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter @Builder
public class FraudRecordDto {
    private final Long fraudId;
    private final String authorizationId;
    private final String action;
    private final String fraudFlag;
    private final String notes;
    private final String actionedBy;
    private final LocalDateTime actionedAt;
}
