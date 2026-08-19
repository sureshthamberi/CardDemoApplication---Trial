package com.carddemo.ops.controller;

import com.carddemo.common.dto.ApiResponse;
import com.carddemo.common.exception.DuplicateResourceException;
import com.carddemo.ops.dto.JobTriggerRequest;
import com.carddemo.ops.entity.JobRunEntity;
import com.carddemo.ops.repository.JobRunRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/ops/jobs")
@Tag(name = "Ops Job Triggers", description = "Background job trigger APIs")
@RequiredArgsConstructor
public class OpsController {

    private static final Set<String> VALID_JOBS = Set.of(
            "daily-transaction-validation",
            "daily-transaction-posting",
            "interest-processing",
            "account-transformation",
            "export",
            "import",
            "statements",
            "transaction-report",
            "pending-auth-cleanup",
            "pending-auth-migration",
            "transaction-type-maintenance"
    );

    private final JobRunRepository jobRunRepository;
    private final ObjectMapper objectMapper;

    @PostMapping("/{jobName}")
    @Operation(summary = "Trigger Background Job")
    public ResponseEntity<ApiResponse<Map<String, Object>>> trigger(
            @PathVariable String jobName,
            @Valid @RequestBody JobTriggerRequest request,
            @AuthenticationPrincipal String principal) {

        if (!VALID_JOBS.contains(jobName)) {
            throw new com.carddemo.common.exception.BusinessRuleException("JOB-400-001", "Unsupported job: " + jobName);
        }

        // Check if already running
        jobRunRepository.findFirstByJobNameAndStatusOrderByCreatedAtDesc(jobName, "RUNNING")
                .ifPresent(j -> { throw new DuplicateResourceException("JOB-409-001", "Job already running"); });

        String jobRunId = "JOB" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();

        String paramsJson;
        try {
            paramsJson = request.getParameters() != null ? objectMapper.writeValueAsString(request.getParameters()) : null;
        } catch (Exception e) { paramsJson = null; }

        JobRunEntity entity = JobRunEntity.builder()
                .jobRunId(jobRunId)
                .jobName(jobName)
                .runMode(request.getRunMode())
                .status("ACCEPTED")
                .triggeredBy(principal)
                .parameters(paramsJson)
                .startedAt(LocalDateTime.now())
                .createdBy(principal)
                .build();

        jobRunRepository.save(entity);
        log.info("Job triggered: {} runId: {} by: {}", jobName, jobRunId, principal);

        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(ApiResponse.ok("Job accepted", Map.of(
                        "jobName", jobName,
                        "jobRunId", jobRunId,
                        "status", "ACCEPTED")));
    }
}
