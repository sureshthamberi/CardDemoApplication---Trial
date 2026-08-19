package com.carddemo.card.repository;

import com.carddemo.card.entity.AccountCardLinkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AccountCardLinkRepository extends JpaRepository<AccountCardLinkEntity, Long> {
    Optional<AccountCardLinkEntity> findFirstByAccountId(String accountId);
    Optional<AccountCardLinkEntity> findFirstByCardNumber(String cardNumber);
    Optional<AccountCardLinkEntity> findByAccountIdAndCardNumber(String accountId, String cardNumber);
}
