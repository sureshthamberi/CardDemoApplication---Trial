package com.carddemo.card.repository;

import com.carddemo.card.entity.CardEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<CardEntity, String> {
    Optional<CardEntity> findByCardNumber(String cardNumber);
    Page<CardEntity> findByAccountId(String accountId, Pageable pageable);
    Page<CardEntity> findByCardNumberContaining(String cardNumber, Pageable pageable);
    Page<CardEntity> findByAccountIdAndCardNumber(String accountId, String cardNumber, Pageable pageable);
}
