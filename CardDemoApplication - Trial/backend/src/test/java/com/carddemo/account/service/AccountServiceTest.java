package com.carddemo.account.service;

import com.carddemo.account.dto.AccountCompositeResponse;
import com.carddemo.account.dto.UpdateAccountRequest;
import com.carddemo.account.entity.AccountEntity;
import com.carddemo.account.entity.CustomerEntity;
import com.carddemo.account.repository.AccountRepository;
import com.carddemo.account.repository.CustomerRepository;
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

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AccountService Unit Tests")
class AccountServiceTest {

    @Mock AccountRepository accountRepository;
    @Mock CustomerRepository customerRepository;
    @InjectMocks AccountService accountService;

    private AccountEntity account;
    private CustomerEntity customer;

    @BeforeEach
    void setUp() {
        account = AccountEntity.builder()
                .accountId("12345678901")
                .customerId("CUST001")
                .accountStatus("ACTIVE")
                .currentBalance(new BigDecimal("450.75"))
                .creditLimit(new BigDecimal("5000.00"))
                .cashCreditLimit(new BigDecimal("1000.00"))
                .currentCycleCredit(BigDecimal.ZERO)
                .currentCycleDebit(BigDecimal.ZERO)
                .version(8L)
                .build();

        customer = CustomerEntity.builder()
                .customerId("CUST001")
                .firstName("Jane")
                .lastName("Doe")
                .ssn("123-45-6789")
                .primaryCardHolderIndicator("Y")
                .version(3L)
                .build();
    }

    @Test
    @DisplayName("getAccount — valid ID returns composite response with masked SSN")
    void getAccount_ValidId_ReturnsMaskedResponse() {
        when(accountRepository.findByAccountId("12345678901")).thenReturn(Optional.of(account));
        when(customerRepository.findByAccountId("12345678901")).thenReturn(Optional.of(customer));

        AccountCompositeResponse response = accountService.getAccount("12345678901");

        assertThat(response.getAccount().getAccountId()).isEqualTo("12345678901");
        assertThat(response.getCustomer().getSsnMasked()).startsWith("***");
        assertThat(response.getCustomer().getSsnMasked()).endsWith("6789");
        assertThat(response.getAccount().getRowVersion()).isEqualTo(8L);
    }

    @Test
    @DisplayName("getAccount — invalid format throws BusinessRuleException")
    void getAccount_InvalidFormat_ThrowsException() {
        assertThatThrownBy(() -> accountService.getAccount("123"))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("11-digit");
    }

    @Test
    @DisplayName("getAccount — account not found throws ResourceNotFoundException")
    void getAccount_NotFound_ThrowsException() {
        when(accountRepository.findByAccountId("12345678901")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> accountService.getAccount("12345678901"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("updateAccount — rowVersion mismatch throws ConcurrencyConflictException")
    void updateAccount_VersionMismatch_ThrowsConflict() {
        when(accountRepository.findByAccountId("12345678901")).thenReturn(Optional.of(account));
        when(customerRepository.findByAccountId("12345678901")).thenReturn(Optional.of(customer));

        UpdateAccountRequest req = new UpdateAccountRequest();
        req.setAccountRowVersion(99L); // mismatch
        req.setCustomerRowVersion(3L);

        assertThatThrownBy(() -> accountService.updateAccount("12345678901", req, "USR001"))
                .isInstanceOf(ConcurrencyConflictException.class);
    }
}
