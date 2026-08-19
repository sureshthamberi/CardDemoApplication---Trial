package com.carddemo.report.repository;

import com.carddemo.report.entity.ReportRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ReportRequestRepository extends JpaRepository<ReportRequestEntity, String> {
    Optional<ReportRequestEntity> findByRequestId(String requestId);
}
