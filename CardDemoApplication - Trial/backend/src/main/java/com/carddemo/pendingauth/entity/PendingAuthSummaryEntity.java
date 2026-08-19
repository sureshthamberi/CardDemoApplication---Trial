package com.carddemo.pendingauth.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pending_auth_summaries")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PendingAuthSummaryEntity {
    @Id
    @Column(name = "account_id", length = 11)
    private String accountId;

    @Column(name = "count", nullable = false)
    private Integer count;

    @Column(name = "total_amount", precision = 18, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "updated_by", length = 64, nullable = false)
    private String updatedBy;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;
}
