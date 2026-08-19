package com.carddemo.user.dto;

import lombok.Builder;
import lombok.Getter;

/** Full user detail DTO (single-record response). */
@Getter
@Builder
public class UserDetailDto {
    private final String userId;
    private final String firstName;
    private final String lastName;
    private final String userType;
    private final String status;
    private final Long   rowVersion;
}
