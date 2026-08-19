package com.carddemo.account.dto;

import lombok.Builder;
import lombok.Getter;

/** Composite account + customer response. LLD 4.6.1 */
@Getter
@Builder
public class AccountCompositeResponse {
    private final AccountDto  account;
    private final CustomerDto customer;
}
