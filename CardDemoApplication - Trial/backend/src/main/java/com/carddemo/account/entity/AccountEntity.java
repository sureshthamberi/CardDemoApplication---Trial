package com.carddemo.account.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountEntity {

    @Id
    @Column(name = "account_id", length = 11)
    private String accountId;

    @Column(name = "customer_id", length = 32)
    private String customerId;

    @Column(name = "account_status", length = 30, nullable = false)
    private String accountStatus;

    @Column(name = "current_balance", precision = 18, scale = 2, nullable = false)
    private BigDecimal currentBalance;

    @Column(name = "credit_limit", precision = 18, scale = 2, nullable = false)
    private BigDecimal creditLimit;

    @Column(name = "cash_credit_limit", precision = 18, scale = 2, nullable = false)
    private BigDecimal cashCreditLimit;

    @Column(name = "current_cycle_credit", precision = 18, scale = 2, nullable = false)
    private BigDecimal currentCycleCredit;

    @Column(name = "current_cycle_debit", precision = 18, scale = 2, nullable = false)
    private BigDecimal currentCycleDebit;

    @Column(name = "open_date")
    private LocalDate openDate;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    @Column(name = "reissue_date")
    private LocalDate reissueDate;

    @Column(name = "group_id", length = 64)
    private String groupId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", length = 64, nullable = false)
    private String createdBy;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "updated_by", length = 64, nullable = false)
    private String updatedBy;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;
}
