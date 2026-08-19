package com.carddemo.account.service;

import com.carddemo.account.dto.*;
import com.carddemo.account.entity.AccountEntity;
import com.carddemo.account.entity.CustomerEntity;
import com.carddemo.account.repository.AccountRepository;
import com.carddemo.account.repository.CustomerRepository;
import com.carddemo.common.exception.BusinessRuleException;
import com.carddemo.common.exception.ConcurrencyConflictException;
import com.carddemo.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Account service — composite account/customer inquiry and update.
 * Implements LLD Section 2.5 / API 4.6.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository  accountRepository;
    private final CustomerRepository customerRepository;

    // ------------------------------------------------------------------
    // Account Inquiry — LLD 4.6.1
    // ------------------------------------------------------------------
    @Transactional(readOnly = true)
    public AccountCompositeResponse getAccount(String accountId) {
        validateAccountId(accountId);

        AccountEntity account = accountRepository.findByAccountId(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("ACC-404-001", "Account/customer not found"));

        CustomerEntity customer = customerRepository.findByAccountId(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("ACC-404-001", "Account/customer not found"));

        return AccountCompositeResponse.builder()
                .account(toAccountDto(account))
                .customer(toCustomerDto(customer))
                .build();
    }

    // ------------------------------------------------------------------
    // Account Update — LLD 4.6.2
    // ------------------------------------------------------------------
    @Transactional
    public UpdateAccountResult updateAccount(String accountId, UpdateAccountRequest req, String actor) {
        validateAccountId(accountId);

        AccountEntity  account  = accountRepository.findByAccountId(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("ACC-404-001", "Account not found"));
        CustomerEntity customer = customerRepository.findByAccountId(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("ACC-404-001", "Customer not found"));

        // Optimistic locking
        if (!Objects.equals(account.getVersion(), req.getAccountRowVersion())) {
            throw new ConcurrencyConflictException("ACC-409-001", "Record changed by someone else");
        }
        if (!Objects.equals(customer.getVersion(), req.getCustomerRowVersion())) {
            throw new ConcurrencyConflictException("ACC-409-001", "Record changed by someone else");
        }

        // Apply account-level changes
        boolean changed = false;
        UpdateAccountRequest.AccountUpdateFields af = req.getAccount();
        if (af != null) {
            if (af.getAccountStatus() != null && !af.getAccountStatus().equals(account.getAccountStatus())) {
                account.setAccountStatus(af.getAccountStatus()); changed = true;
            }
            if (af.getCreditLimit() != null && !af.getCreditLimit().equals(account.getCreditLimit())) {
                account.setCreditLimit(af.getCreditLimit()); changed = true;
            }
            if (af.getCashCreditLimit() != null && !af.getCashCreditLimit().equals(account.getCashCreditLimit())) {
                account.setCashCreditLimit(af.getCashCreditLimit()); changed = true;
            }
            if (af.getExpirationDate() != null && !af.getExpirationDate().equals(account.getExpirationDate())) {
                account.setExpirationDate(af.getExpirationDate()); changed = true;
            }
            if (af.getReissueDate() != null && !af.getReissueDate().equals(account.getReissueDate())) {
                account.setReissueDate(af.getReissueDate()); changed = true;
            }
            if (af.getGroupId() != null && !af.getGroupId().equals(account.getGroupId())) {
                account.setGroupId(af.getGroupId()); changed = true;
            }
        }

        // Apply customer-level changes
        UpdateAccountRequest.CustomerUpdateFields cf = req.getCustomer();
        if (cf != null) {
            if (cf.getFirstName() != null && !cf.getFirstName().equals(customer.getFirstName())) {
                customer.setFirstName(cf.getFirstName()); changed = true;
            }
            if (cf.getLastName() != null && !cf.getLastName().equals(customer.getLastName())) {
                customer.setLastName(cf.getLastName()); changed = true;
            }
            if (cf.getMiddleName() != null) { customer.setMiddleName(cf.getMiddleName()); }
            if (cf.getAddressLine1() != null) { customer.setAddressLine1(cf.getAddressLine1()); changed = true; }
            if (cf.getAddressLine2() != null) { customer.setAddressLine2(cf.getAddressLine2()); }
            if (cf.getCity() != null) { customer.setCity(cf.getCity()); changed = true; }
            if (cf.getState() != null) { customer.setState(cf.getState()); changed = true; }
            if (cf.getZip() != null) { customer.setZip(cf.getZip()); changed = true; }
            if (cf.getCountry() != null) { customer.setCountry(cf.getCountry()); }
            if (cf.getPhone1() != null) { customer.setPhone1(cf.getPhone1()); }
            if (cf.getPhone2() != null) { customer.setPhone2(cf.getPhone2()); }
            if (cf.getCreditScore() != null && !cf.getCreditScore().equals(customer.getCreditScore())) {
                customer.setCreditScore(cf.getCreditScore()); changed = true;
            }
            if (cf.getDateOfBirth() != null) { customer.setDateOfBirth(cf.getDateOfBirth()); }
            if (cf.getGovernmentId() != null) { customer.setGovernmentId(cf.getGovernmentId()); }
            if (cf.getElectronicFundsAccountRef() != null) {
                customer.setElectronicFundsAccountRef(cf.getElectronicFundsAccountRef());
            }
            if (cf.getPrimaryCardHolderIndicator() != null) {
                customer.setPrimaryCardHolderIndicator(cf.getPrimaryCardHolderIndicator()); changed = true;
            }
        }

        if (!changed) {
            throw new BusinessRuleException("ACC-400-002", "No change detected");
        }

        account.setUpdatedBy(actor);
        account.setUpdatedAt(LocalDateTime.now());
        customer.setUpdatedBy(actor);
        customer.setUpdatedAt(LocalDateTime.now());

        AccountEntity  savedAccount  = accountRepository.save(account);
        CustomerEntity savedCustomer = customerRepository.save(customer);

        log.info("Account updated: {} by: {}", accountId, actor);

        return UpdateAccountResult.builder()
                .accountId(accountId)
                .accountRowVersion(savedAccount.getVersion())
                .customerRowVersion(savedCustomer.getVersion())
                .build();
    }

    // ------------------------------------------------------------------
    // Account balance helper — used by PaymentService
    // ------------------------------------------------------------------
    @Transactional(readOnly = true)
    public AccountEntity findAccountOrFail(String accountId) {
        validateAccountId(accountId);
        return accountRepository.findByAccountId(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("ACC-404-001", "Account not found"));
    }

    @Transactional
    public void saveAccount(AccountEntity account) {
        accountRepository.save(account);
    }

    // ------------------------------------------------------------------
    // Private helpers
    // ------------------------------------------------------------------

    private void validateAccountId(String accountId) {
        if (accountId == null || !accountId.matches("[1-9][0-9]{10}")) {
            throw new com.carddemo.common.exception.BusinessRuleException(
                    "ACC-VAL-001", "Account must be a non-zero 11-digit number");
        }
    }

    private AccountDto toAccountDto(AccountEntity e) {
        return AccountDto.builder()
                .accountId(e.getAccountId())
                .accountStatus(e.getAccountStatus())
                .currentBalance(e.getCurrentBalance())
                .creditLimit(e.getCreditLimit())
                .cashCreditLimit(e.getCashCreditLimit())
                .currentCycleCredit(e.getCurrentCycleCredit())
                .currentCycleDebit(e.getCurrentCycleDebit())
                .openDate(e.getOpenDate())
                .expirationDate(e.getExpirationDate())
                .reissueDate(e.getReissueDate())
                .groupId(e.getGroupId())
                .rowVersion(e.getVersion())
                .build();
    }

    private CustomerDto toCustomerDto(CustomerEntity e) {
        return CustomerDto.builder()
                .customerId(e.getCustomerId())
                .ssnMasked(maskSsn(e.getSsn()))
                .creditScore(e.getCreditScore())
                .dateOfBirth(e.getDateOfBirth())
                .firstName(e.getFirstName())
                .middleName(e.getMiddleName())
                .lastName(e.getLastName())
                .addressLine1(e.getAddressLine1())
                .addressLine2(e.getAddressLine2())
                .city(e.getCity())
                .state(e.getState())
                .zip(e.getZip())
                .country(e.getCountry())
                .phone1(e.getPhone1())
                .phone2(e.getPhone2())
                .governmentIdMasked(maskGovernmentId(e.getGovernmentId()))
                .electronicFundsAccountRef(e.getElectronicFundsAccountRef())
                .primaryCardHolderIndicator(e.getPrimaryCardHolderIndicator())
                .rowVersion(e.getVersion())
                .build();
    }

    private String maskSsn(String ssn) {
        if (ssn == null || ssn.length() < 4) return "***";
        return "***-**-" + ssn.substring(ssn.length() - 4);
    }

    private String maskGovernmentId(String govId) {
        if (govId == null || govId.length() < 5) return "***";
        return "***" + govId.substring(govId.length() - 5);
    }
}
