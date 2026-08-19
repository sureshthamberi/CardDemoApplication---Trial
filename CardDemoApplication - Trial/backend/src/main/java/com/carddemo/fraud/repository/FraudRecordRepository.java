package com.carddemo.fraud.repository;

import com.carddemo.fraud.entity.FraudRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FraudRecordRepository extends JpaRepository<FraudRecordEntity, Long> {
    List<FraudRecordEntity> findByAuthorizationIdOrderByActionedAtDesc(String authorizationId);
    Optional<FraudRecordEntity> findFirstByAuthorizationIdOrderByActionedAtDesc(String authorizationId);
}
