package com.carddemo.card.service;

import com.carddemo.card.dto.*;
import com.carddemo.card.entity.CardEntity;
import com.carddemo.card.repository.CardRepository;
import com.carddemo.common.dto.PageResponse;
import com.carddemo.common.dto.PageResponse.Pagination;
import com.carddemo.common.exception.BusinessRuleException;
import com.carddemo.common.exception.ConcurrencyConflictException;
import com.carddemo.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;

    @Transactional(readOnly = true)
    public PageResponse<CardSummaryDto> searchCards(String accountId, String cardNumber, int page, int pageSize) {
        PageRequest pageable = PageRequest.of(page - 1, pageSize);
        Page<CardEntity> entityPage;

        boolean hasAccount = StringUtils.hasText(accountId);
        boolean hasCard    = StringUtils.hasText(cardNumber);

        if (hasAccount && hasCard) {
            entityPage = cardRepository.findByAccountIdAndCardNumber(accountId, cardNumber, pageable);
        } else if (hasAccount) {
            entityPage = cardRepository.findByAccountId(accountId, pageable);
        } else if (hasCard) {
            entityPage = cardRepository.findByCardNumberContaining(cardNumber, pageable);
        } else {
            entityPage = cardRepository.findAll(pageable);
        }

        List<CardSummaryDto> items = entityPage.getContent().stream().map(this::toSummary).toList();
        return PageResponse.<CardSummaryDto>builder()
                .items(items)
                .pagination(Pagination.builder()
                        .page(page).pageSize(pageSize)
                        .hasNext(entityPage.hasNext()).hasPrevious(entityPage.hasPrevious())
                        .totalElements(entityPage.getTotalElements()).build())
                .build();
    }

    @Transactional(readOnly = true)
    public CardDetailDto getCard(String cardNumber) {
        if (cardNumber == null || !cardNumber.matches("[0-9]{16}")) {
            throw new BusinessRuleException("CARD-VAL-001", "Card number must be 16 digits");
        }
        CardEntity card = cardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new ResourceNotFoundException("CARD-404-001", "Card not found"));
        return toDetail(card);
    }

    @Transactional
    public long updateCard(String cardNumber, UpdateCardRequest req, String actor) {
        CardEntity card = cardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new ResourceNotFoundException("CARD-404-001", "Card not found"));

        if (!Objects.equals(card.getVersion(), req.getRowVersion())) {
            throw new ConcurrencyConflictException("CARD-409-001", "Record changed by someone else");
        }

        boolean changed = false;
        if (!req.getCardName().equals(card.getCardName())) { card.setCardName(req.getCardName()); changed = true; }
        if (!req.getActiveStatus().equals(card.getActiveStatus())) { card.setActiveStatus(req.getActiveStatus()); changed = true; }
        if (!req.getExpiryMonth().equals(card.getExpiryMonth())) { card.setExpiryMonth(req.getExpiryMonth()); changed = true; }
        if (!req.getExpiryYear().equals(card.getExpiryYear())) { card.setExpiryYear(req.getExpiryYear()); changed = true; }

        if (!changed) {
            throw new BusinessRuleException("CARD-400-001", "No change detected");
        }

        card.setUpdatedBy(actor);
        card.setUpdatedAt(LocalDateTime.now());
        CardEntity saved = cardRepository.save(card);
        log.info("Card updated: {} by: {}", cardNumber, actor);
        return saved.getVersion();
    }

    private CardSummaryDto toSummary(CardEntity e) {
        return CardSummaryDto.builder()
                .accountId(e.getAccountId())
                .cardNumber(e.getCardNumber())
                .cardNumberMasked(maskCard(e.getCardNumber()))
                .cardName(e.getCardName())
                .expiryMonth(e.getExpiryMonth())
                .expiryYear(e.getExpiryYear())
                .cardStatus(e.getCardStatus())
                .activeStatus(e.getActiveStatus())
                .build();
    }

    private CardDetailDto toDetail(CardEntity e) {
        return CardDetailDto.builder()
                .accountId(e.getAccountId())
                .cardNumberMasked(maskCard(e.getCardNumber()))
                .cardName(e.getCardName())
                .expiryMonth(e.getExpiryMonth())
                .expiryYear(e.getExpiryYear())
                .cardStatus(e.getCardStatus())
                .activeStatus(e.getActiveStatus())
                .rowVersion(e.getVersion())
                .build();
    }

    private String maskCard(String card) {
        if (card == null || card.length() < 4) return "****";
        return "************" + card.substring(card.length() - 4);
    }
}
