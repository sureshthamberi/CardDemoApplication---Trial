package com.carddemo.payment.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "idempotency_keys")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class IdempotencyKeyEntity {
    @Id
    @Column(name = "idempotency_key", length = 128)
    private String idempotencyKey;

    @Column(name = "operation", length = 64, nullable = false)
    private String operation;

    @Column(name = "response_payload", columnDefinition = "CLOB")
    private String responsePayload;

    @Column(name = "http_status")
    private Integer httpStatus;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
}
