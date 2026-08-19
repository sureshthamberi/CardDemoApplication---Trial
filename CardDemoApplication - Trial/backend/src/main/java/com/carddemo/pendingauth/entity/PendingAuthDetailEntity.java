package com.carddemo.pendingauth.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pending_auth_details")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PendingAuthDetailEntity {
    @Id
    @Column(name = "authorization_id", length = 64)
    private String authorizationId;

    @Column(name = "account_id", length = 11)
    private String accountId;

    @Column(name = "card_number", length = 16)
    private String cardNumber;

    @Column(name = "amount", precision = 18, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "merchant_id", length = 64)
    private String merchantId;

    @Column(name = "merchant_name", length = 255)
    private String merchantName;

    @Column(name = "merchant_city", length = 100)
    private String merchantCity;

    @Column(name = "merchant_zip", length = 20)
    private String merchantZip;

    @Column(name = "status", length = 30, nullable = false)
    private String status;

    @Column(name = "fraud_flag", length = 1, nullable = false)
    private String fraudFlag;

    @Column(name = "request_timestamp")
    private LocalDateTime requestTimestamp;

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
