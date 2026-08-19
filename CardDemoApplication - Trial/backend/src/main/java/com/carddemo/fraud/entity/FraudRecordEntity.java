package com.carddemo.fraud.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "fraud_records")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FraudRecordEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fraud_id")
    private Long fraudId;

    @Column(name = "authorization_id", length = 64, nullable = false)
    private String authorizationId;

    @Column(name = "action", length = 10, nullable = false)
    private String action;

    @Column(name = "fraud_flag", length = 1, nullable = false)
    private String fraudFlag;

    @Column(name = "notes", length = 500)
    private String notes;

    @Column(name = "actioned_by", length = 64)
    private String actionedBy;

    @Column(name = "actioned_at", nullable = false)
    private LocalDateTime actionedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", length = 64, nullable = false)
    private String createdBy;
}
