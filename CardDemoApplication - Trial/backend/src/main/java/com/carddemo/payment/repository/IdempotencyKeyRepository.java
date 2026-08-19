package com.carddemo.payment.repository;

import com.carddemo.payment.entity.IdempotencyKeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface IdempotencyKeyRepository extends JpaRepository<IdempotencyKeyEntity, String> {
    Optional<IdempotencyKeyEntity> findByIdempotencyKey(String key);
}
