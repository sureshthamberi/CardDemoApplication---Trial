package com.carddemo.user.dto;

import lombok.Builder;
import lombok.Getter;

/** User summary DTO (list response). */
@Getter
@Builder
public class UserSummaryDto {
    private final String userId;
    private final String firstName;
    private final String lastName;
    private final String userType;
    private final String status;
}
