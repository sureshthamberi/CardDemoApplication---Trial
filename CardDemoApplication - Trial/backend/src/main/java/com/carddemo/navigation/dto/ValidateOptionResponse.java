package com.carddemo.navigation.dto;

import lombok.Builder;
import lombok.Getter;

/** Response payload for validate-option. */
@Getter
@Builder
public class ValidateOptionResponse {
    private final boolean valid;
    private final String  targetPage;
    private final String  targetRoute;
}
