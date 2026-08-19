package com.carddemo.account.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

/** Customer section of composite account response. SSN/GovernmentId masked. */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerDto {
    private final String  customerId;
    private final String  ssnMasked;
    private final Integer creditScore;
    private final LocalDate dateOfBirth;
    private final String  firstName;
    private final String  middleName;
    private final String  lastName;
    private final String  addressLine1;
    private final String  addressLine2;
    private final String  city;
    private final String  state;
    private final String  zip;
    private final String  country;
    private final String  phone1;
    private final String  phone2;
    private final String  governmentIdMasked;
    private final String  electronicFundsAccountRef;
    private final String  primaryCardHolderIndicator;
    private final Long    rowVersion;
}
