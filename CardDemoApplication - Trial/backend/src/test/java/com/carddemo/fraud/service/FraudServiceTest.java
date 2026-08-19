package com.carddemo.fraud.service;

import com.carddemo.common.exception.ResourceNotFoundException;
import com.carddemo.fraud.dto.FraudActionRequest;
import com.carddemo.fraud.dto.FraudActionResponse;
import com.carddemo.fraud.dto.FraudRecordDto;
import com.carddemo.fraud.entity.FraudRecordEntity;
import com.carddemo.fraud.repository.FraudRecordRepository;
import com.carddemo.pendingauth.entity.PendingAuthDetailEntity;
import com.carddemo.pendingauth.repository.PendingAuthDetailRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FraudService Unit Tests")
class FraudServiceTest {

    @Mock FraudRecordRepository       fraudRepo;
    @Mock PendingAuthDetailRepository pendingAuthDetailRepo;

    @InjectMocks FraudService fraudService;

    private PendingAuthDetailEntity pendingAuth;

    @BeforeEach
    void setUp() {
        pendingAuth = PendingAuthDetailEntity.builder()
                .authorizationId("AUTH001")
                .accountId("12345678901")
                .cardNumber("4444333322221111")
                .amount(new BigDecimal("100.50"))
                .merchantId("M001")
                .merchantName("ABC STORE")
                .merchantCity("Austin")
                .merchantZip("78701")
                .status("PENDING")
                .fraudFlag("N")
                .requestTimestamp(LocalDateTime.of(2026, 8, 5, 9, 0))
                .createdBy("SYSTEM")
                .updatedBy("SYSTEM")
                .version(0L)
                .build();
    }

    // ─── markFraud ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("markFraud — sets fraudFlag to Y and saves fraud record")
    void markFraud_setsFlagY_andSavesRecord() {
        when(pendingAuthDetailRepo.findByAuthorizationId("AUTH001")).thenReturn(Optional.of(pendingAuth));

        FraudActionRequest req = new FraudActionRequest();
        req.setNotes("Suspicious activity");

        FraudActionResponse response = fraudService.markFraud("AUTH001", req, "USR001");

        assertThat(response.getAuthorizationId()).isEqualTo("AUTH001");
        assertThat(response.getFraudFlag()).isEqualTo("Y");
        assertThat(response.getAction()).isEqualTo("MARK");

        verify(pendingAuthDetailRepo).save(argThat(a -> "Y".equals(a.getFraudFlag())));
        verify(fraudRepo).save(argThat(r ->
                "MARK".equals(r.getAction()) && "Y".equals(r.getFraudFlag())
        ));
    }

    @Test
    @DisplayName("markFraud — throws ResourceNotFoundException when authorization not found")
    void markFraud_throwsNotFound_whenAuthMissing() {
        when(pendingAuthDetailRepo.findByAuthorizationId("AUTH999")).thenReturn(Optional.empty());

        FraudActionRequest req = new FraudActionRequest();
        assertThatThrownBy(() -> fraudService.markFraud("AUTH999", req, "USR001"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Authorization not found");
    }

    // ─── unmarkFraud ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("unmarkFraud — sets fraudFlag to N and saves UNMARK record")
    void unmarkFraud_setsFlagN_andSavesRecord() {
        pendingAuth.setFraudFlag("Y");
        when(pendingAuthDetailRepo.findByAuthorizationId("AUTH001")).thenReturn(Optional.of(pendingAuth));

        FraudActionRequest req = new FraudActionRequest();
        req.setNotes("Resolved — not fraud");

        FraudActionResponse response = fraudService.unmarkFraud("AUTH001", req, "USR001");

        assertThat(response.getFraudFlag()).isEqualTo("N");
        assertThat(response.getAction()).isEqualTo("UNMARK");

        verify(pendingAuthDetailRepo).save(argThat(a -> "N".equals(a.getFraudFlag())));
        verify(fraudRepo).save(argThat(r ->
                "UNMARK".equals(r.getAction()) && "N".equals(r.getFraudFlag())
        ));
    }

    @Test
    @DisplayName("unmarkFraud — throws ResourceNotFoundException when authorization not found")
    void unmarkFraud_throwsNotFound_whenAuthMissing() {
        when(pendingAuthDetailRepo.findByAuthorizationId("AUTH999")).thenReturn(Optional.empty());

        FraudActionRequest req = new FraudActionRequest();
        assertThatThrownBy(() -> fraudService.unmarkFraud("AUTH999", req, "USR001"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── getFraudRecords ─────────────────────────────────────────────────────

    @Test
    @DisplayName("getFraudRecords — returns list of fraud history DTOs ordered by actioned_at desc")
    void getFraudRecords_returnsMappedList() {
        FraudRecordEntity record = FraudRecordEntity.builder()
                .fraudId(1L)
                .authorizationId("AUTH001")
                .action("MARK")
                .fraudFlag("Y")
                .notes("Test note")
                .actionedBy("USR001")
                .actionedAt(LocalDateTime.of(2026, 8, 5, 12, 0))
                .createdBy("SYSTEM")
                .build();

        when(fraudRepo.findByAuthorizationIdOrderByActionedAtDesc("AUTH001")).thenReturn(List.of(record));

        List<FraudRecordDto> result = fraudService.getFraudRecords("AUTH001");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAction()).isEqualTo("MARK");
        assertThat(result.get(0).getFraudFlag()).isEqualTo("Y");
        assertThat(result.get(0).getActionedBy()).isEqualTo("USR001");
    }
}
