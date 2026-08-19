package com.carddemo.payment.service;

import com.carddemo.account.entity.AccountEntity;
import com.carddemo.account.repository.AccountRepository;
import com.carddemo.common.exception.BusinessRuleException;
import com.carddemo.common.exception.ResourceNotFoundException;
import com.carddemo.payment.dto.*;
import com.carddemo.payment.entity.IdempotencyKeyEntity;
import com.carddemo.payment.repository.IdempotencyKeyRepository;
import com.carddemo.transaction.entity.TransactionEntity;
import com.carddemo.transaction.repository.TransactionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final IdempotencyKeyRepository idempotencyKeyRepository;
    private final ObjectMapper objectMapper;

    public BillPaymentPreviewDto previewPayment(String accountId) {
        validateAccountId(accountId);
        AccountEntity account = accountRepository.findByAccountId(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("ACC-404-001", "Account not found"));
        boolean payable = account.getCurrentBalance().compareTo(BigDecimal.ZERO) > 0;
        if (!payable) throw new BusinessRuleException("PAY-400-001", "You have nothing to pay");
        return BillPaymentPreviewDto.builder()
                .accountId(accountId)
                .currentBalance(account.getCurrentBalance())
                .payable(true)
                .build();
    }

    @Transactional
    public BillPaymentResultDto createPayment(BillPaymentRequest req, String actor, String idempotencyKey) {
        validateAccountId(req.getAccountId());

        // Idempotency check
        if (idempotencyKey != null) {
            var prior = idempotencyKeyRepository.findByIdempotencyKey(idempotencyKey);
            if (prior.isPresent()) {
                try {
                    return objectMapper.readValue(prior.get().getResponsePayload(), BillPaymentResultDto.class);
                } catch (Exception e) { log.warn("Could not deserialise idempotency payload", e); }
            }
        }

        AccountEntity account = accountRepository.findByAccountId(req.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("ACC-404-001", "Account not found"));

        if (account.getCurrentBalance().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleException("PAY-400-002", "You have nothing to pay");
        }

        BigDecimal amount = account.getCurrentBalance();
        String txnId = "TXN" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();

        TransactionEntity txn = TransactionEntity.builder()
                .transactionId(txnId)
                .accountId(req.getAccountId())
                .transactionType("PAYMENT")
                .categoryType("PAYMENT")
                .source("MANUAL")
                .amount(amount)
                .description("Bill Payment")
                .originalTimestamp(LocalDateTime.now())
                .processedTimestamp(LocalDateTime.now())
                .createdBy(actor)
                .updatedBy(actor)
                .version(0L)
                .build();

        transactionRepository.save(txn);
        account.setCurrentBalance(BigDecimal.ZERO);
        account.setUpdatedBy(actor);
        accountRepository.save(account);

        BillPaymentResultDto result = BillPaymentResultDto.builder()
                .accountId(req.getAccountId())
                .paymentAmount(amount)
                .transactionId(txnId)
                .remainingBalance(BigDecimal.ZERO)
                .build();

        if (idempotencyKey != null) {
            try {
                IdempotencyKeyEntity idem = IdempotencyKeyEntity.builder()
                        .idempotencyKey(idempotencyKey)
                        .operation("BILL_PAYMENT")
                        .responsePayload(objectMapper.writeValueAsString(result))
                        .httpStatus(201)
                        .createdAt(LocalDateTime.now())
                        .expiresAt(LocalDateTime.now().plusDays(1))
                        .build();
                idempotencyKeyRepository.save(idem);
            } catch (Exception e) { log.warn("Could not persist idempotency record", e); }
        }
        log.info("Bill payment created: {} accountId: {} amount: {} by: {}", txnId, req.getAccountId(), amount, actor);
        return result;
    }

    private void validateAccountId(String accountId) {
        if (accountId == null || !accountId.matches("[1-9][0-9]{10}")) {
            throw new BusinessRuleException("ACC-VAL-001", "Account must be a non-zero 11-digit number");
        }
    }
}
