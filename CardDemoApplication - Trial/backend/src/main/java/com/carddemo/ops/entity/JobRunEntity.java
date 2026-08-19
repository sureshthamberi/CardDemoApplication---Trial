package com.carddemo.ops.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "job_runs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class JobRunEntity {
    @Id
    @Column(name = "job_run_id", length = 64)
    private String jobRunId;

    @Column(name = "job_name", length = 100, nullable = false)
    private String jobName;

    @Column(name = "run_mode", length = 20, nullable = false)
    private String runMode;

    @Column(name = "status", length = 20, nullable = false)
    private String status;

    @Column(name = "triggered_by", length = 64)
    private String triggeredBy;

    @Column(name = "parameters", length = 1000)
    private String parameters;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "error_message", length = 2000)
    private String errorMessage;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", length = 64, nullable = false)
    private String createdBy;
}
