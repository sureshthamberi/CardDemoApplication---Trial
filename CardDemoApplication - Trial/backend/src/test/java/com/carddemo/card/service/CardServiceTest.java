package com.carddemo.card.service;

import com.carddemo.card.dto.CardDetailDto;
import com.carddemo.card.dto.UpdateCardRequest;
import com.carddemo.card.entity.CardEntity;
import com.carddemo.card.repository.CardRepository;
import com.carddemo.common.exception.BusinessRuleException;
import com.carddemo.common.exception.ConcurrencyConflictException;
import com.carddemo.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CardService Unit Tests")
class CardServiceTest {

    @Mock CardRepository cardRepository;
    @InjectMocks CardService cardService;

    private CardEntity card;

    @BeforeEach
    void setUp() {
        card = CardEntity.builder()
                .cardNumber("4444333322221111")
                .accountId("12345678901")
                .cardName("JANE DOE")
                .expiryMonth(12)
                .expiryYear(2028)
                .cardStatus("ACTIVE")
                .activeStatus("Y")
                .version(2L)
                .build();
    }

    @Test
    @DisplayName("getCard — masks card number correctly")
    void getCard_MasksCardNumber() {
        when(cardRepository.findByCardNumber("4444333322221111")).thenReturn(Optional.of(card));
        CardDetailDto detail = cardService.getCard("4444333322221111");
        assertThat(detail.getCardNumberMasked()).isEqualTo("************1111");
        assertThat(detail.getRowVersion()).isEqualTo(2L);
    }

    @Test
    @DisplayName("getCard — invalid format throws BusinessRuleException")
    void getCard_InvalidFormat_ThrowsException() {
        assertThatThrownBy(() -> cardService.getCard("1234"))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    @DisplayName("getCard — not found throws ResourceNotFoundException")
    void getCard_NotFound_ThrowsException() {
        when(cardRepository.findByCardNumber(anyString())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> cardService.getCard("4444333322221111"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("updateCard — no-change detected throws BusinessRuleException")
    void updateCard_NoChange_ThrowsException() {
        when(cardRepository.findByCardNumber("4444333322221111")).thenReturn(Optional.of(card));
        UpdateCardRequest req = new UpdateCardRequest();
        req.setAccountId("12345678901"); req.setCardName("JANE DOE");
        req.setActiveStatus("Y"); req.setExpiryMonth(12); req.setExpiryYear(2028);
        req.setRowVersion(2L);

        assertThatThrownBy(() -> cardService.updateCard("4444333322221111", req, "USR001"))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("No change detected");
    }

    @Test
    @DisplayName("updateCard — version conflict throws ConcurrencyConflictException")
    void updateCard_VersionConflict_ThrowsException() {
        when(cardRepository.findByCardNumber("4444333322221111")).thenReturn(Optional.of(card));
        UpdateCardRequest req = new UpdateCardRequest();
        req.setAccountId("12345678901"); req.setCardName("JANE DOE");
        req.setActiveStatus("Y"); req.setExpiryMonth(12); req.setExpiryYear(2028);
        req.setRowVersion(99L); // version mismatch

        assertThatThrownBy(() -> cardService.updateCard("4444333322221111", req, "USR001"))
                .isInstanceOf(ConcurrencyConflictException.class);
    }
}
