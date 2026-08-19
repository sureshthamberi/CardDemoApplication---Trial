package com.carddemo.pendingauth.repository;

import com.carddemo.pendingauth.entity.PendingAuthSummaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PendingAuthSummaryRepository extends JpaRepository<PendingAuthSummaryEntity, String> {
    Optional<PendingAuthSummaryEntity> findByAccountId(String accountId);
}
