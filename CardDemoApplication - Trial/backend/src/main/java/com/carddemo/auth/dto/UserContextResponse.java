package com.carddemo.auth.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/** Current-user context response payload (GET /api/v1/auth/me). */
@Getter
@Builder
public class UserContextResponse {
    private final String       userId;
    private final String       displayName;
    private final String       userType;
    private final List<String> permissions;
}
