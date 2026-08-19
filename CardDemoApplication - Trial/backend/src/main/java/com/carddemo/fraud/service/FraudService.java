package com.carddemo.fraud.service;

import com.carddemo.common.exception.ResourceNotFoundException;
import com.carddemo.fraud.dto.*;
import com.carddemo.fraud.entity.FraudRecordEntity;
import com.carddemo.fraud.repository.FraudRecordRepository;
import com.carddemo.pendingauth.entity.PendingAuthDetailEntity;
import com.carddemo.pendingauth.repository.PendingAuthDetailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FraudService {

    private final FraudRecordRepository fraudRepo;
    private final PendingAuthDetailRepository pendingAuthDetailRepo;

    @Transactional
    public FraudActionResponse markFraud(String authorizationId, FraudActionRequest req, String actor) {
        PendingAuthDetailEntity auth = pendingAuthDetailRepo.findByAuthorizationId(authorizationId)
                .orElseThrow(() -> new ResourceNotFoundException("PAUTH-404-001", "Authorization not found"));

        auth.setFraudFlag("Y");
        auth.setUpdatedBy(actor);
        auth.setUpdatedAt(LocalDateTime.now());
        pendingAuthDetailRepo.save(auth);

        FraudRecordEntity record = FraudRecordEntity.builder()
                .authorizationId(authorizationId)
                .action("MARK")
                .fraudFlag("Y")
                .notes(req.getNotes())
                .actionedBy(actor)
                .actionedAt(LocalDateTime.now())
                .createdBy(actor)
                .build();
        fraudRepo.save(record);

        log.info("Fraud marked for authorizationId: {} by: {}", authorizationId, actor);
        return FraudActionResponse.builder()
                .authorizationId(authorizationId)
                .fraudFlag("Y")
                .action("MARK")
                .build();
    }

    @Transactional
    public FraudActionResponse unmarkFraud(String authorizationId, FraudActionRequest req, String actor) {
        PendingAuthDetailEntity auth = pendingAuthDetailRepo.findByAuthorizationId(authorizationId)
                .orElseThrow(() -> new ResourceNotFoundException("PAUTH-404-001", "Authorization not found"));

        auth.setFraudFlag("N");
        auth.setUpdatedBy(actor);
        auth.setUpdatedAt(LocalDateTime.now());
        pendingAuthDetailRepo.save(auth);

        FraudRecordEntity record = FraudRecordEntity.builder()
                .authorizationId(authorizationId)
                .action("UNMARK")
                .fraudFlag("N")
                .notes(req.getNotes())
                .actionedBy(actor)
                .actionedAt(LocalDateTime.now())
                .createdBy(actor)
                .build();
        fraudRepo.save(record);

        log.info("Fraud unmarked for authorizationId: {} by: {}", authorizationId, actor);
        return FraudActionResponse.builder()
                .authorizationId(authorizationId)
                .fraudFlag("N")
                .action("UNMARK")
                .build();
    }

    @Transactional(readOnly = true)
    public List<FraudRecordDto> getFraudRecords(String authorizationId) {
        return fraudRepo.findByAuthorizationIdOrderByActionedAtDesc(authorizationId).stream()
                .map(f -> FraudRecordDto.builder()
                        .fraudId(f.getFraudId())
                        .authorizationId(f.getAuthorizationId())
                        .action(f.getAction())
                        .fraudFlag(f.getFraudFlag())
                        .notes(f.getNotes())
                        .actionedBy(f.getActionedBy())
                        .actionedAt(f.getActionedAt())
                        .build())
                .toList();
    }
}
