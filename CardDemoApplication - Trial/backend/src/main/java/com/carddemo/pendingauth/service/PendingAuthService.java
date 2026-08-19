package com.carddemo.pendingauth.service;

import com.carddemo.account.entity.AccountEntity;
import com.carddemo.account.entity.CustomerEntity;
import com.carddemo.account.repository.AccountRepository;
import com.carddemo.account.repository.CustomerRepository;
import com.carddemo.common.dto.PageResponse;
import com.carddemo.common.dto.PageResponse.Pagination;
import com.carddemo.common.exception.ResourceNotFoundException;
import com.carddemo.pendingauth.dto.*;
import com.carddemo.pendingauth.entity.PendingAuthDetailEntity;
import com.carddemo.pendingauth.entity.PendingAuthSummaryEntity;
import com.carddemo.pendingauth.repository.PendingAuthDetailRepository;
import com.carddemo.pendingauth.repository.PendingAuthSummaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PendingAuthService {

    private final PendingAuthSummaryRepository summaryRepo;
    private final PendingAuthDetailRepository  detailRepo;
    private final AccountRepository  accountRepo;
    private final CustomerRepository customerRepo;

    @Transactional(readOnly = true)
    public PendingAuthSummaryResponse getAccountSummary(String accountId, int page, int pageSize) {
        AccountEntity account = accountRepo.findByAccountId(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("ACC-404-001", "Account not found"));
        CustomerEntity customer = customerRepo.findByAccountId(accountId).orElse(null);
        PendingAuthSummaryEntity summary = summaryRepo.findByAccountId(accountId).orElse(null);

        PageRequest pageable = PageRequest.of(page - 1, pageSize);
        Page<PendingAuthDetailEntity> detailPage =
                detailRepo.findByAccountIdOrderByRequestTimestampDesc(accountId, pageable);

        List<PendingAuthItemDto> items = detailPage.getContent().stream()
                .map(d -> PendingAuthItemDto.builder()
                        .authorizationId(d.getAuthorizationId())
                        .amount(d.getAmount())
                        .merchantName(d.getMerchantName())
                        .status(d.getStatus())
                        .fraudFlag(d.getFraudFlag())
                        .build())
                .toList();

        return PendingAuthSummaryResponse.builder()
                .accountSummary(PendingAuthSummaryResponse.AccountSummaryDto.builder()
                        .accountId(account.getAccountId())
                        .accountStatus(account.getAccountStatus())
                        .build())
                .customerSummary(customer != null ? PendingAuthSummaryResponse.CustomerSummaryDto.builder()
                        .customerId(customer.getCustomerId())
                        .firstName(customer.getFirstName())
                        .lastName(customer.getLastName())
                        .build() : null)
                .pendingAuthorizationSummary(summary != null ? PendingAuthSummaryResponse.AuthSummaryDto.builder()
                        .count(summary.getCount())
                        .totalAmount(summary.getTotalAmount())
                        .build() : PendingAuthSummaryResponse.AuthSummaryDto.builder().count(0).totalAmount(java.math.BigDecimal.ZERO).build())
                .items(items)
                .pagination(Pagination.builder()
                        .page(page).pageSize(pageSize)
                        .hasNext(detailPage.hasNext()).hasPrevious(detailPage.hasPrevious())
                        .totalElements(detailPage.getTotalElements()).build())
                .build();
    }

    @Transactional(readOnly = true)
    public PendingAuthDetailDto getDetail(String authorizationId) {
        PendingAuthDetailEntity d = detailRepo.findByAuthorizationId(authorizationId)
                .orElseThrow(() -> new ResourceNotFoundException("PAUTH-404-001", "Authorization not found"));
        return toDetailDto(d);
    }

    @Transactional(readOnly = true)
    public PendingAuthDetailDto getNext(String authorizationId) {
        PendingAuthDetailEntity current = detailRepo.findByAuthorizationId(authorizationId)
                .orElseThrow(() -> new ResourceNotFoundException("PAUTH-404-001", "Authorization not found"));
        PendingAuthDetailEntity next = detailRepo.findNextAfter(current.getAccountId(), authorizationId)
                .orElseThrow(() -> new ResourceNotFoundException("PAUTH-404-002", "No next authorization"));
        return toDetailDto(next);
    }

    private PendingAuthDetailDto toDetailDto(PendingAuthDetailEntity d) {
        return PendingAuthDetailDto.builder()
                .authorizationId(d.getAuthorizationId())
                .accountId(d.getAccountId())
                .cardNumberMasked(maskCard(d.getCardNumber()))
                .amount(d.getAmount())
                .merchantId(d.getMerchantId())
                .merchantName(d.getMerchantName())
                .merchantCity(d.getMerchantCity())
                .merchantZip(d.getMerchantZip())
                .status(d.getStatus())
                .fraudFlag(d.getFraudFlag())
                .requestTimestamp(d.getRequestTimestamp())
                .build();
    }

    private String maskCard(String card) {
        if (card == null || card.length() < 4) return "****";
        return "************" + card.substring(card.length() - 4);
    }
}
