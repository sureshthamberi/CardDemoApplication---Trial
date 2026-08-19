package com.carddemo.auth.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

/** Login success response payload. */
@Getter
@Builder
public class LoginResponse {

    private final String userId;
    private final String displayName;
    private final String userType;
    private final String landingPage;
    private final String token;
    private final String expiresAt;
}
