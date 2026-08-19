package com.carddemo.card.dto;

import lombok.Builder;
import lombok.Getter;

@Getter @Builder
public class CardSummaryDto {
    private final String  accountId;
    /** Unmasked card number — used for Detail/Update action links in the UI. */
    private final String  cardNumber;
    /** Masked display value — shown in the search results table. */
    private final String  cardNumberMasked;
    private final String  cardName;
    private final Integer expiryMonth;
    private final Integer expiryYear;
    private final String  cardStatus;
    private final String  activeStatus;
}
