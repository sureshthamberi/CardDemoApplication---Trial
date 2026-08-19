package com.carddemo.reference.repository;

import com.carddemo.reference.entity.TransactionTypeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TransactionTypeRepository extends JpaRepository<TransactionTypeEntity, String> {
    Optional<TransactionTypeEntity> findByTypeCode(String typeCode);
    boolean existsByTypeCode(String typeCode);
    Page<TransactionTypeEntity> findByDescriptionContainingIgnoreCase(String description, Pageable pageable);
    Page<TransactionTypeEntity> findByTypeCodeContainingIgnoreCase(String typeCode, Pageable pageable);
}
