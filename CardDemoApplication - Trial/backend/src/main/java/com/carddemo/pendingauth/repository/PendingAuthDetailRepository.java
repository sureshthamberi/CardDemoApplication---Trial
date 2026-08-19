package com.carddemo.pendingauth.repository;

import com.carddemo.pendingauth.entity.PendingAuthDetailEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PendingAuthDetailRepository extends JpaRepository<PendingAuthDetailEntity, String> {
    Optional<PendingAuthDetailEntity> findByAuthorizationId(String authorizationId);
    Page<PendingAuthDetailEntity> findByAccountIdOrderByRequestTimestampDesc(String accountId, Pageable pageable);

    @Query("SELECT p FROM PendingAuthDetailEntity p WHERE p.accountId = :accountId AND p.authorizationId > :currentId ORDER BY p.authorizationId ASC")
    Optional<PendingAuthDetailEntity> findNextAfter(String accountId, String currentId);
}
