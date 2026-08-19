package com.carddemo.internal.dto;

import lombok.Builder;
import lombok.Getter;

@Getter @Builder
public class AuthDecisionResponse {
    private final String decision;
    private final String responseCode;
    private final String authorizationId;
}
