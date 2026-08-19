package com.carddemo.pendingauth.dto;

import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;
import java.util.List;

@Getter @Builder
public class PendingAuthSummaryResponse {
    private final AccountSummaryDto accountSummary;
    private final CustomerSummaryDto customerSummary;
    private final AuthSummaryDto pendingAuthorizationSummary;
    private final List<PendingAuthItemDto> items;
    private final com.carddemo.common.dto.PageResponse.Pagination pagination;

    @Getter @Builder
    public static class AccountSummaryDto {
        private final String accountId;
        private final String accountStatus;
    }
    @Getter @Builder
    public static class CustomerSummaryDto {
        private final String customerId;
        private final String firstName;
        private final String lastName;
    }
    @Getter @Builder
    public static class AuthSummaryDto {
        private final int count;
        private final BigDecimal totalAmount;
    }
}
