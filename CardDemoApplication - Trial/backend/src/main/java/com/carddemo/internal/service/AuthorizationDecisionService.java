package com.carddemo.internal.service;

import com.carddemo.account.entity.AccountEntity;
import com.carddemo.account.repository.AccountRepository;
import com.carddemo.card.repository.AccountCardLinkRepository;
import com.carddemo.common.exception.BusinessRuleException;
import com.carddemo.common.exception.ResourceNotFoundException;
import com.carddemo.internal.dto.*;
import com.carddemo.pendingauth.entity.PendingAuthDetailEntity;
import com.carddemo.pendingauth.entity.PendingAuthSummaryEntity;
import com.carddemo.pendingauth.repository.PendingAuthDetailRepository;
import com.carddemo.pendingauth.repository.PendingAuthSummaryRepository;
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
public class AuthorizationDecisionService {

    private final AccountRepository accountRepo;
    private final AccountCardLinkRepository linkRepo;
    private final PendingAuthSummaryRepository summaryRepo;
    private final PendingAuthDetailRepository detailRepo;

    @Transactional
    public AuthDecisionResponse decide(AuthDecisionRequest req) {
        AccountEntity account = accountRepo.findByAccountId(req.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("ACC-404-001", "Account not found"));

        // Validate linkage
        boolean linked = linkRepo.findByAccountIdAndCardNumber(req.getAccountId(), req.getCardNumber()).isPresent();
        if (!linked) {
            log.warn("Authorization declined — linkage not found: account={} card=****{}", req.getAccountId(), req.getCardNumber().substring(12));
            return AuthDecisionResponse.builder().decision("DECLINE").responseCode("05").authorizationId(null).build();
        }

        // Check available credit
        BigDecimal available = account.getCreditLimit().subtract(account.getCurrentBalance());
        if (available.compareTo(req.getAmount()) < 0) {
            log.warn("Authorization declined — insufficient credit: account={}", req.getAccountId());
            return AuthDecisionResponse.builder().decision("DECLINE").responseCode("51").authorizationId(null).build();
        }

        String authId = "AUTH" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();

        // Update pending auth summary
        PendingAuthSummaryEntity summary = summaryRepo.findByAccountId(req.getAccountId())
                .orElse(PendingAuthSummaryEntity.builder()
                        .accountId(req.getAccountId())
                        .count(0)
                        .totalAmount(BigDecimal.ZERO)
                        .updatedBy("SYSTEM")
                        .version(0L)
                        .build());
        summary.setCount(summary.getCount() + 1);
        summary.setTotalAmount(summary.getTotalAmount().add(req.getAmount()));
        summary.setUpdatedBy("SYSTEM");
        summaryRepo.save(summary);

        // Create pending auth detail
        PendingAuthDetailEntity detail = PendingAuthDetailEntity.builder()
                .authorizationId(authId)
                .accountId(req.getAccountId())
                .cardNumber(req.getCardNumber())
                .amount(req.getAmount())
                .merchantId(req.getMerchantId())
                .merchantName(req.getMerchantName())
                .merchantCity(req.getMerchantCity())
                .merchantZip(req.getMerchantZip())
                .status("PENDING")
                .fraudFlag("N")
                .requestTimestamp(req.getRequestTimestamp() != null ? req.getRequestTimestamp() : LocalDateTime.now())
                .createdBy("SYSTEM")
                .updatedBy("SYSTEM")
                .version(0L)
                .build();
        detailRepo.save(detail);

        log.info("Authorization APPROVED: authId={} account={} amount={}", authId, req.getAccountId(), req.getAmount());
        return AuthDecisionResponse.builder().decision("APPROVE").responseCode("00").authorizationId(authId).build();
    }
}
