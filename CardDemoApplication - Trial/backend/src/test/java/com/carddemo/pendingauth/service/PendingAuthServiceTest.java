package com.carddemo.pendingauth.service;

import com.carddemo.account.entity.AccountEntity;
import com.carddemo.account.entity.CustomerEntity;
import com.carddemo.account.repository.AccountRepository;
import com.carddemo.account.repository.CustomerRepository;
import com.carddemo.common.exception.ResourceNotFoundException;
import com.carddemo.pendingauth.dto.PendingAuthDetailDto;
import com.carddemo.pendingauth.dto.PendingAuthSummaryResponse;
import com.carddemo.pendingauth.entity.PendingAuthDetailEntity;
import com.carddemo.pendingauth.entity.PendingAuthSummaryEntity;
import com.carddemo.pendingauth.repository.PendingAuthDetailRepository;
import com.carddemo.pendingauth.repository.PendingAuthSummaryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PendingAuthService Unit Tests")
class PendingAuthServiceTest {

    @Mock PendingAuthSummaryRepository summaryRepo;
    @Mock PendingAuthDetailRepository  detailRepo;
    @Mock AccountRepository            accountRepo;
    @Mock CustomerRepository           customerRepo;

    @InjectMocks PendingAuthService pendingAuthService;

    private AccountEntity              account;
    private CustomerEntity             customer;
    private PendingAuthSummaryEntity   summary;
    private PendingAuthDetailEntity    authDetail;

    @BeforeEach
    void setUp() {
        account = AccountEntity.builder()
                .accountId("12345678901")
                .accountStatus("ACTIVE")
                .currentBalance(new BigDecimal("450.75"))
                .creditLimit(new BigDecimal("5000.00"))
                .cashCreditLimit(new BigDecimal("1000.00"))
                .version(0L)
                .createdBy("SYSTEM")
                .updatedBy("SYSTEM")
                .build();

        customer = CustomerEntity.builder()
                .customerId("CUST001")
                .firstName("Jane")
                .lastName("Doe")
                .version(0L)
                .createdBy("SYSTEM")
                .updatedBy("SYSTEM")
                .build();

        summary = PendingAuthSummaryEntity.builder()
                .accountId("12345678901")
                .count(3)
                .totalAmount(new BigDecimal("275.24"))
                .updatedBy("SYSTEM")
                .version(0L)
                .build();

        authDetail = PendingAuthDetailEntity.builder()
                .authorizationId("AUTH001")
                .accountId("12345678901")
                .cardNumber("4444333322221111")
                .amount(new BigDecimal("100.50"))
                .merchantId("M001")
                .merchantName("ABC STORE")
                .merchantCity("Austin")
                .merchantZip("78701")
                .status("PENDING")
                .fraudFlag("N")
                .requestTimestamp(LocalDateTime.of(2026, 8, 5, 9, 0))
                .createdBy("SYSTEM")
                .updatedBy("SYSTEM")
                .version(0L)
                .build();
    }

    // ─── getAccountSummary ───────────────────────────────────────────────────

    @Test
    @DisplayName("getAccountSummary — returns full summary with items and pagination")
    void getAccountSummary_returnsFullSummary() {
        when(accountRepo.findByAccountId("12345678901")).thenReturn(Optional.of(account));
        when(customerRepo.findByAccountId("12345678901")).thenReturn(Optional.of(customer));
        when(summaryRepo.findByAccountId("12345678901")).thenReturn(Optional.of(summary));

        Page<PendingAuthDetailEntity> page = new PageImpl<>(List.of(authDetail));
        when(detailRepo.findByAccountIdOrderByRequestTimestampDesc(eq("12345678901"), any(Pageable.class)))
                .thenReturn(page);

        PendingAuthSummaryResponse result = pendingAuthService.getAccountSummary("12345678901", 1, 20);

        assertThat(result.getAccountSummary().getAccountId()).isEqualTo("12345678901");
        assertThat(result.getCustomerSummary().getFirstName()).isEqualTo("Jane");
        assertThat(result.getPendingAuthorizationSummary().getCount()).isEqualTo(3);
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getAuthorizationId()).isEqualTo("AUTH001");
    }

    @Test
    @DisplayName("getAccountSummary — returns zero summary when no pending auth summary exists")
    void getAccountSummary_returnsZeroSummary_whenNoSummaryRecord() {
        when(accountRepo.findByAccountId("12345678901")).thenReturn(Optional.of(account));
        when(customerRepo.findByAccountId("12345678901")).thenReturn(Optional.empty());
        when(summaryRepo.findByAccountId("12345678901")).thenReturn(Optional.empty());

        Page<PendingAuthDetailEntity> page = new PageImpl<>(List.of());
        when(detailRepo.findByAccountIdOrderByRequestTimestampDesc(eq("12345678901"), any(Pageable.class)))
                .thenReturn(page);

        PendingAuthSummaryResponse result = pendingAuthService.getAccountSummary("12345678901", 1, 20);

        assertThat(result.getPendingAuthorizationSummary().getCount()).isEqualTo(0);
        assertThat(result.getPendingAuthorizationSummary().getTotalAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.getItems()).isEmpty();
    }

    @Test
    @DisplayName("getAccountSummary — throws ResourceNotFoundException when account not found")
    void getAccountSummary_throwsNotFound_whenAccountMissing() {
        when(accountRepo.findByAccountId("99999999999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pendingAuthService.getAccountSummary("99999999999", 1, 20))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Account not found");
    }

    // ─── getDetail ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("getDetail — returns detail DTO with masked card number")
    void getDetail_returnsDetailDto_withMaskedCard() {
        when(detailRepo.findByAuthorizationId("AUTH001")).thenReturn(Optional.of(authDetail));

        PendingAuthDetailDto result = pendingAuthService.getDetail("AUTH001");

        assertThat(result.getAuthorizationId()).isEqualTo("AUTH001");
        assertThat(result.getCardNumberMasked()).isEqualTo("************1111");
        assertThat(result.getAmount()).isEqualByComparingTo("100.50");
        assertThat(result.getFraudFlag()).isEqualTo("N");
    }

    @Test
    @DisplayName("getDetail — throws ResourceNotFoundException when not found")
    void getDetail_throwsNotFound_whenMissing() {
        when(detailRepo.findByAuthorizationId("AUTH999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pendingAuthService.getDetail("AUTH999"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── getNext ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getNext — returns next authorization in sequence")
    void getNext_returnsNextAuth() {
        PendingAuthDetailEntity next = PendingAuthDetailEntity.builder()
                .authorizationId("AUTH002")
                .accountId("12345678901")
                .cardNumber("4444333322221111")
                .amount(new BigDecimal("50.25"))
                .status("PENDING")
                .fraudFlag("N")
                .createdBy("SYSTEM")
                .updatedBy("SYSTEM")
                .version(0L)
                .build();

        when(detailRepo.findByAuthorizationId("AUTH001")).thenReturn(Optional.of(authDetail));
        when(detailRepo.findNextAfter("12345678901", "AUTH001")).thenReturn(Optional.of(next));

        PendingAuthDetailDto result = pendingAuthService.getNext("AUTH001");

        assertThat(result.getAuthorizationId()).isEqualTo("AUTH002");
        assertThat(result.getAmount()).isEqualByComparingTo("50.25");
    }

    @Test
    @DisplayName("getNext — throws ResourceNotFoundException when no next authorization exists")
    void getNext_throwsNotFound_whenNoNext() {
        when(detailRepo.findByAuthorizationId("AUTH001")).thenReturn(Optional.of(authDetail));
        when(detailRepo.findNextAfter("12345678901", "AUTH001")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pendingAuthService.getNext("AUTH001"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No next authorization");
    }
}
