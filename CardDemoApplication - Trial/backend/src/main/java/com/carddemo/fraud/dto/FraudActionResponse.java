package com.carddemo.fraud.dto;

import lombok.Builder;
import lombok.Getter;

@Getter @Builder
public class FraudActionResponse {
    private final String authorizationId;
    private final String fraudFlag;
    private final String action;
}
