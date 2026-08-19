package com.carddemo.transaction.service;

import com.carddemo.card.repository.AccountCardLinkRepository;
import com.carddemo.card.entity.AccountCardLinkEntity;
import com.carddemo.common.dto.PageResponse;
import com.carddemo.common.exception.BusinessRuleException;
import com.carddemo.common.exception.ResourceNotFoundException;
import com.carddemo.transaction.dto.CreateTransactionRequest;
import com.carddemo.transaction.dto.TransactionDetailDto;
import com.carddemo.transaction.dto.TransactionSummaryDto;
import com.carddemo.transaction.entity.TransactionEntity;
import com.carddemo.transaction.repository.TransactionRepository;
import com.carddemo.account.repository.AccountRepository;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TransactionService Unit Tests")
class TransactionServiceTest {

    @Mock TransactionRepository      transactionRepository;
    @Mock AccountRepository          accountRepository;
    @Mock AccountCardLinkRepository  accountCardLinkRepository;

    @InjectMocks TransactionService transactionService;

    private TransactionEntity sampleTxn;

    @BeforeEach
    void setUp() {
        sampleTxn = TransactionEntity.builder()
                .transactionId("TXN1001")
                .accountId("12345678901")
                .cardNumber("4444333322221111")
                .transactionType("PURCHASE")
                .categoryType("RETAIL")
                .source("ONLINE")
                .amount(new BigDecimal("100.50"))
                .description("Test purchase")
                .originalTimestamp(LocalDateTime.of(2026, 7, 1, 10, 0))
                .processedTimestamp(LocalDateTime.of(2026, 7, 1, 10, 1))
                .merchantId("M001")
                .merchantName("TECH STORE")
                .merchantCity("Austin")
                .merchantZip("78701")
                .version(0L)
                .createdBy("SYSTEM")
                .updatedBy("SYSTEM")
                .build();
    }

    // ─── listTransactions ────────────────────────────────────────────────────

    @Test
    @DisplayName("listTransactions — returns all transactions when no filters given")
    void listTransactions_returnsSummary_withoutFilter() {
        Page<TransactionEntity> page = new PageImpl<>(List.of(sampleTxn));
        when(transactionRepository.findAll(any(Pageable.class))).thenReturn(page);

        PageResponse<TransactionSummaryDto> result = transactionService.listTransactions(1, 20, null, null);

        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getTransactionId()).isEqualTo("TXN1001");
        assertThat(result.getItems().get(0).getAmount()).isEqualByComparingTo("100.50");
    }

    @Test
    @DisplayName("listTransactions — uses startId filter when provided without accountId")
    void listTransactions_usesStartIdFilter_whenProvided() {
        Page<TransactionEntity> page = new PageImpl<>(List.of(sampleTxn));
        when(transactionRepository.findByTransactionIdGreaterThanEqualOrderByTransactionId(
                eq("TXN1001"), any(Pageable.class))).thenReturn(page);

        PageResponse<TransactionSummaryDto> result = transactionService.listTransactions(1, 20, "TXN1001", null);

        assertThat(result.getItems()).hasSize(1);
        verify(transactionRepository).findByTransactionIdGreaterThanEqualOrderByTransactionId(
                eq("TXN1001"), any(Pageable.class));
    }

    @Test
    @DisplayName("listTransactions — filters by accountId when provided")
    void listTransactions_filtersByAccountId() {
        Page<TransactionEntity> page = new PageImpl<>(List.of(sampleTxn));
        when(transactionRepository.findByAccountId(eq("12345678901"), any(Pageable.class))).thenReturn(page);

        PageResponse<TransactionSummaryDto> result = transactionService.listTransactions(1, 20, null, "12345678901");

        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getTransactionId()).isEqualTo("TXN1001");
        verify(transactionRepository).findByAccountId(eq("12345678901"), any(Pageable.class));
    }

    @Test
    @DisplayName("listTransactions — uses accountId + startId combined filter when both provided")
    void listTransactions_usesCombinedFilter_whenBothProvided() {
        Page<TransactionEntity> page = new PageImpl<>(List.of(sampleTxn));
        when(transactionRepository.findByAccountIdAndTransactionIdGreaterThanEqualOrderByTransactionId(
                eq("12345678901"), eq("TXN1001"), any(Pageable.class))).thenReturn(page);

        PageResponse<TransactionSummaryDto> result = transactionService.listTransactions(1, 20, "TXN1001", "12345678901");

        assertThat(result.getItems()).hasSize(1);
        verify(transactionRepository).findByAccountIdAndTransactionIdGreaterThanEqualOrderByTransactionId(
                eq("12345678901"), eq("TXN1001"), any(Pageable.class));
    }

    // ─── getTransaction ──────────────────────────────────────────────────────

    @Test
    @DisplayName("getTransaction — returns detail DTO with masked card number")
    void getTransaction_returnsDetail_withMaskedCard() {
        when(transactionRepository.findByTransactionId("TXN1001")).thenReturn(Optional.of(sampleTxn));

        TransactionDetailDto result = transactionService.getTransaction("TXN1001");

        assertThat(result.getTransactionId()).isEqualTo("TXN1001");
        assertThat(result.getCardNumberMasked()).isEqualTo("************1111");
        assertThat(result.getAmount()).isEqualByComparingTo("100.50");
        assertThat(result.getMerchantName()).isEqualTo("TECH STORE");
    }

    @Test
    @DisplayName("getTransaction — throws ResourceNotFoundException when not found")
    void getTransaction_throwsNotFound_whenMissing() {
        when(transactionRepository.findByTransactionId("TXN9999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.getTransaction("TXN9999"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── createTransaction ───────────────────────────────────────────────────

    @Test
    @DisplayName("createTransaction — resolves card from account link and saves transaction")
    void createTransaction_resolvesCard_andSaves() {
        AccountCardLinkEntity link = AccountCardLinkEntity.builder()
                .accountId("12345678901")
                .cardNumber("4444333322221111")
                .build();
        when(accountCardLinkRepository.findFirstByAccountId("12345678901")).thenReturn(Optional.of(link));

        CreateTransactionRequest req = new CreateTransactionRequest();
        req.setAccountId("12345678901");
        req.setTransactionType("PURCHASE");
        req.setCategoryType("RETAIL");
        req.setSource("ONLINE");
        req.setAmount(new BigDecimal("50.00"));
        req.setDescription("Test");
        req.setOriginalDate(LocalDate.of(2026, 8, 1));
        req.setProcessDate(LocalDate.of(2026, 8, 1));
        req.setConfirmation("Y");

        String txnId = transactionService.createTransaction(req, "USR001");

        assertThat(txnId).startsWith("TXN");
        verify(transactionRepository).save(any(TransactionEntity.class));
    }

    @Test
    @DisplayName("createTransaction — throws BusinessRuleException when neither accountId nor cardNumber given")
    void createTransaction_throwsBusinessRule_whenBothMissing() {
        CreateTransactionRequest req = new CreateTransactionRequest();
        req.setTransactionType("PURCHASE");
        req.setCategoryType("RETAIL");
        req.setSource("ONLINE");
        req.setAmount(new BigDecimal("50.00"));
        req.setDescription("Test");
        req.setOriginalDate(LocalDate.of(2026, 8, 1));
        req.setProcessDate(LocalDate.of(2026, 8, 1));
        req.setConfirmation("Y");

        assertThatThrownBy(() -> transactionService.createTransaction(req, "USR001"))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("accountId or cardNumber");
    }

    // ─── copyLastTransaction ─────────────────────────────────────────────────

    @Test
    @DisplayName("copyLastTransaction — returns field map from most recent transaction")
    void copyLastTransaction_returnsLastTransactionFields() {
        when(transactionRepository.findFirstByAccountIdOrderByCreatedAtDesc("12345678901"))
                .thenReturn(Optional.of(sampleTxn));

        Map<String, Object> result = transactionService.copyLastTransaction("12345678901");

        assertThat(result.get("transactionType")).isEqualTo("PURCHASE");
        assertThat(result.get("merchantName")).isEqualTo("TECH STORE");
        assertThat((BigDecimal) result.get("amount")).isEqualByComparingTo("100.50");
    }

    @Test
    @DisplayName("copyLastTransaction — throws ResourceNotFoundException when no transactions exist")
    void copyLastTransaction_throwsNotFound_whenEmpty() {
        when(transactionRepository.findFirstByAccountIdOrderByCreatedAtDesc("67890123456"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.copyLastTransaction("67890123456"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No transaction found");
    }
}
