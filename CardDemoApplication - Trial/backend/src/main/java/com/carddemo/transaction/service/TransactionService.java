package com.carddemo.transaction.service;

import com.carddemo.account.entity.AccountEntity;
import com.carddemo.account.repository.AccountRepository;
import com.carddemo.card.repository.AccountCardLinkRepository;
import com.carddemo.common.dto.PageResponse;
import com.carddemo.common.dto.PageResponse.Pagination;
import com.carddemo.common.exception.BusinessRuleException;
import com.carddemo.common.exception.ResourceNotFoundException;
import com.carddemo.transaction.dto.*;
import com.carddemo.transaction.entity.TransactionEntity;
import com.carddemo.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final AccountCardLinkRepository accountCardLinkRepository;

    @Transactional(readOnly = true)
    public PageResponse<TransactionSummaryDto> listTransactions(int page, int pageSize, String startId, String accountId) {
        PageRequest pageable = PageRequest.of(page - 1, pageSize, Sort.by("transactionId").ascending());
        boolean hasAccount = StringUtils.hasText(accountId);
        boolean hasStart   = StringUtils.hasText(startId);
        Page<TransactionEntity> entityPage;
        if (hasAccount && hasStart) {
            entityPage = transactionRepository
                    .findByAccountIdAndTransactionIdGreaterThanEqualOrderByTransactionId(
                            accountId.trim(), startId.trim(), pageable);
        } else if (hasAccount) {
            entityPage = transactionRepository.findByAccountId(accountId.trim(), pageable);
        } else if (hasStart) {
            entityPage = transactionRepository.findByTransactionIdGreaterThanEqualOrderByTransactionId(startId.trim(), pageable);
        } else {
            entityPage = transactionRepository.findAll(pageable);
        }
        List<TransactionSummaryDto> items = entityPage.getContent().stream().map(this::toSummary).toList();
        return PageResponse.<TransactionSummaryDto>builder()
                .items(items)
                .pagination(Pagination.builder()
                        .page(page).pageSize(pageSize)
                        .hasNext(entityPage.hasNext()).hasPrevious(entityPage.hasPrevious())
                        .totalElements(entityPage.getTotalElements()).build())
                .build();
    }

    @Transactional(readOnly = true)
    public TransactionDetailDto getTransaction(String transactionId) {
        TransactionEntity e = transactionRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("TXN-404-001", "Transaction not found"));
        return toDetail(e);
    }

    @Transactional
    public String createTransaction(CreateTransactionRequest req, String actor) {
        if (!StringUtils.hasText(req.getAccountId()) && !StringUtils.hasText(req.getCardNumber())) {
            throw new BusinessRuleException("TXN-VAL-001", "Either accountId or cardNumber must be provided");
        }

        String resolvedAccountId = req.getAccountId();
        String resolvedCardNumber = req.getCardNumber();

        // Resolve missing account/card via linkage
        if (StringUtils.hasText(resolvedAccountId) && !StringUtils.hasText(resolvedCardNumber)) {
            resolvedCardNumber = accountCardLinkRepository
                    .findFirstByAccountId(resolvedAccountId)
                    .map(l -> l.getCardNumber()).orElse(null);
        } else if (!StringUtils.hasText(resolvedAccountId) && StringUtils.hasText(resolvedCardNumber)) {
            String finalCard = resolvedCardNumber;
            resolvedAccountId = accountCardLinkRepository
                    .findFirstByCardNumber(finalCard)
                    .map(l -> l.getAccountId()).orElse(null);
        }

        if (!StringUtils.hasText(resolvedAccountId)) {
            throw new BusinessRuleException("TXN-400-001", "Account/card linkage not found");
        }

        String txnId = "TXN" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();

        TransactionEntity entity = TransactionEntity.builder()
                .transactionId(txnId)
                .accountId(resolvedAccountId)
                .cardNumber(resolvedCardNumber)
                .transactionType(req.getTransactionType())
                .categoryType(req.getCategoryType())
                .source(req.getSource())
                .amount(req.getAmount())
                .description(req.getDescription())
                .originalTimestamp(req.getOriginalDate().atStartOfDay())
                .processedTimestamp(req.getProcessDate().atStartOfDay())
                .merchantId(req.getMerchantId())
                .merchantName(req.getMerchantName())
                .merchantCity(req.getMerchantCity())
                .merchantZip(req.getMerchantZip())
                .createdBy(actor)
                .updatedBy(actor)
                .version(0L)
                .build();

        transactionRepository.save(entity);
        log.info("Transaction created: {} by: {}", txnId, actor);
        return txnId;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> copyLastTransaction(String accountId) {
        TransactionEntity last = transactionRepository.findFirstByAccountIdOrderByCreatedAtDesc(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("TXN-404-001", "No transaction found for account"));
        return Map.of(
                "transactionType", last.getTransactionType() != null ? last.getTransactionType() : "",
                "categoryType",    last.getCategoryType() != null ? last.getCategoryType() : "",
                "source",          last.getSource() != null ? last.getSource() : "",
                "amount",          last.getAmount(),
                "description",     last.getDescription() != null ? last.getDescription() : "",
                "merchantId",      last.getMerchantId() != null ? last.getMerchantId() : "",
                "merchantName",    last.getMerchantName() != null ? last.getMerchantName() : "",
                "merchantCity",    last.getMerchantCity() != null ? last.getMerchantCity() : "",
                "merchantZip",     last.getMerchantZip() != null ? last.getMerchantZip() : ""
        );
    }

    private TransactionSummaryDto toSummary(TransactionEntity e) {
        return TransactionSummaryDto.builder()
                .transactionId(e.getTransactionId())
                .date(e.getOriginalTimestamp())
                .description(e.getDescription())
                .amount(e.getAmount())
                .transactionType(e.getTransactionType())
                .build();
    }

    private TransactionDetailDto toDetail(TransactionEntity e) {
        return TransactionDetailDto.builder()
                .transactionId(e.getTransactionId())
                .cardNumberMasked(maskCard(e.getCardNumber()))
                .typeCode(e.getTransactionType())
                .categoryCode(e.getCategoryType())
                .source(e.getSource())
                .amount(e.getAmount())
                .description(e.getDescription())
                .originalTimestamp(e.getOriginalTimestamp())
                .processedTimestamp(e.getProcessedTimestamp())
                .merchantId(e.getMerchantId())
                .merchantName(e.getMerchantName())
                .merchantCity(e.getMerchantCity())
                .merchantZip(e.getMerchantZip())
                .build();
    }

    private String maskCard(String card) {
        if (card == null || card.length() < 4) return "****";
        return "************" + card.substring(card.length() - 4);
    }
}
