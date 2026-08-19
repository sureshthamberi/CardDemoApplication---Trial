package com.carddemo.card.dto;

import lombok.Builder;
import lombok.Getter;

@Getter @Builder
public class CardDetailDto {
    private final String  accountId;
    private final String  cardNumberMasked;
    private final String  cardName;
    private final Integer expiryMonth;
    private final Integer expiryYear;
    private final String  cardStatus;
    private final String  activeStatus;
    private final Long    rowVersion;
}
