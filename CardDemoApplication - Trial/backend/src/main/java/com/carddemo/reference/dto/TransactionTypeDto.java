package com.carddemo.reference.dto;

import lombok.Builder;
import lombok.Getter;

@Getter @Builder
public class TransactionTypeDto {
    private final String typeCode;
    private final String description;
    private final Long rowVersion;
}
