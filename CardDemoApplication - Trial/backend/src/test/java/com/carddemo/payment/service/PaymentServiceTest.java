package com.carddemo.payment.service;

import com.carddemo.account.entity.AccountEntity;
import com.carddemo.account.repository.AccountRepository;
import com.carddemo.common.exception.BusinessRuleException;
import com.carddemo.common.exception.ResourceNotFoundException;
import com.carddemo.payment.dto.BillPaymentPreviewDto;
import com.carddemo.payment.dto.BillPaymentRequest;
import com.carddemo.payment.dto.BillPaymentResultDto;
import com.carddemo.payment.entity.IdempotencyKeyEntity;
import com.carddemo.payment.repository.IdempotencyKeyRepository;
import com.carddemo.transaction.repository.TransactionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentService Unit Tests")
class PaymentServiceTest {

    @Mock AccountRepository       accountRepository;
    @Mock TransactionRepository   transactionRepository;
    @Mock IdempotencyKeyRepository idempotencyKeyRepository;
    @Mock ObjectMapper             objectMapper;

    @InjectMocks PaymentService paymentService;

    private AccountEntity activeAccount;

    @BeforeEach
    void setUp() {
        activeAccount = AccountEntity.builder()
                .accountId("12345678901")
                .accountStatus("ACTIVE")
                .currentBalance(new BigDecimal("450.75"))
                .creditLimit(new BigDecimal("5000.00"))
                .cashCreditLimit(new BigDecimal("1000.00"))
                .version(0L)
                .createdBy("SYSTEM")
                .updatedBy("SYSTEM")
                .build();
    }

    // ─── previewPayment ──────────────────────────────────────────────────────

    @Test
    @DisplayName("previewPayment — returns preview DTO when balance > 0")
    void previewPayment_returnsPreview_whenBalancePositive() {
        when(accountRepository.findByAccountId("12345678901")).thenReturn(Optional.of(activeAccount));

        BillPaymentPreviewDto result = paymentService.previewPayment("12345678901");

        assertThat(result.getAccountId()).isEqualTo("12345678901");
        assertThat(result.getCurrentBalance()).isEqualByComparingTo("450.75");
        assertThat(result.isPayable()).isTrue();
    }

    @Test
    @DisplayName("previewPayment — throws BusinessRuleException when balance is zero")
    void previewPayment_throwsBusinessRule_whenBalanceZero() {
        activeAccount.setCurrentBalance(BigDecimal.ZERO);
        when(accountRepository.findByAccountId("12345678901")).thenReturn(Optional.of(activeAccount));

        assertThatThrownBy(() -> paymentService.previewPayment("12345678901"))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("nothing to pay");
    }

    @Test
    @DisplayName("previewPayment — throws BusinessRuleException for invalid account ID format")
    void previewPayment_throwsBusinessRule_whenAccountIdInvalid() {
        assertThatThrownBy(() -> paymentService.previewPayment("00000000000"))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("non-zero 11-digit");
    }

    @Test
    @DisplayName("previewPayment — throws ResourceNotFoundException when account does not exist")
    void previewPayment_throwsNotFound_whenAccountMissing() {
        when(accountRepository.findByAccountId("12345678902")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.previewPayment("12345678902"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── createPayment ───────────────────────────────────────────────────────

    @Test
    @DisplayName("createPayment — creates transaction and zeroes balance on happy path")
    void createPayment_createsTransaction_onHappyPath() throws Exception {
        BillPaymentRequest req = new BillPaymentRequest();
        req.setAccountId("12345678901");
        req.setConfirmation("Y");

        when(idempotencyKeyRepository.findByIdempotencyKey(anyString())).thenReturn(Optional.empty());
        when(accountRepository.findByAccountId("12345678901")).thenReturn(Optional.of(activeAccount));

        BillPaymentResultDto result = paymentService.createPayment(req, "USR001", "idem-key-1");

        assertThat(result.getAccountId()).isEqualTo("12345678901");
        assertThat(result.getPaymentAmount()).isEqualByComparingTo("450.75");
        assertThat(result.getRemainingBalance()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.getTransactionId()).isNotBlank();

        verify(transactionRepository).save(any());
        verify(accountRepository).save(argThat(a -> a.getCurrentBalance().compareTo(BigDecimal.ZERO) == 0));
    }

    @Test
    @DisplayName("createPayment — returns cached response when idempotency key already exists")
    void createPayment_returnsCached_whenIdempotencyKeyExists() throws Exception {
        BillPaymentRequest req = new BillPaymentRequest();
        req.setAccountId("12345678901");
        req.setConfirmation("Y");

        BillPaymentResultDto cached = BillPaymentResultDto.builder()
                .accountId("12345678901")
                .paymentAmount(new BigDecimal("450.75"))
                .transactionId("TXN_CACHED")
                .remainingBalance(BigDecimal.ZERO)
                .build();

        IdempotencyKeyEntity existing = IdempotencyKeyEntity.builder()
                .idempotencyKey("idem-key-dup")
                .operation("BILL_PAYMENT")
                .responsePayload("{}")
                .httpStatus(201)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(1))
                .build();

        when(idempotencyKeyRepository.findByIdempotencyKey("idem-key-dup")).thenReturn(Optional.of(existing));
        when(objectMapper.readValue("{}", BillPaymentResultDto.class)).thenReturn(cached);

        BillPaymentResultDto result = paymentService.createPayment(req, "USR001", "idem-key-dup");

        assertThat(result.getTransactionId()).isEqualTo("TXN_CACHED");
        verify(transactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("createPayment — throws BusinessRuleException when balance is zero")
    void createPayment_throwsBusinessRule_whenBalanceZero() {
        BillPaymentRequest req = new BillPaymentRequest();
        req.setAccountId("12345678901");
        req.setConfirmation("Y");

        activeAccount.setCurrentBalance(BigDecimal.ZERO);
        when(idempotencyKeyRepository.findByIdempotencyKey(anyString())).thenReturn(Optional.empty());
        when(accountRepository.findByAccountId("12345678901")).thenReturn(Optional.of(activeAccount));

        assertThatThrownBy(() -> paymentService.createPayment(req, "USR001", "idem-key-new"))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("nothing to pay");

        verify(transactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("createPayment — throws BusinessRuleException for invalid account ID format")
    void createPayment_throwsBusinessRule_whenAccountIdInvalid() {
        BillPaymentRequest req = new BillPaymentRequest();
        req.setAccountId("00000000000");
        req.setConfirmation("Y");

        assertThatThrownBy(() -> paymentService.createPayment(req, "USR001", null))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("non-zero 11-digit");
    }
}
