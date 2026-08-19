package com.carddemo.account.dto;

import lombok.Builder;
import lombok.Getter;

/** Result returned after updating an account composite. */
@Getter
@Builder
public class UpdateAccountResult {
    private final String accountId;
    private final Long   accountRowVersion;
    private final Long   customerRowVersion;
}
