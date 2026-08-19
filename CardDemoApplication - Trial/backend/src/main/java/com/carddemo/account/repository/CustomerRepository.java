package com.carddemo.account.repository;

import com.carddemo.account.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, String> {

    @Query("SELECT c FROM CustomerEntity c JOIN AccountEntity a ON a.customerId = c.customerId WHERE a.accountId = :accountId")
    Optional<CustomerEntity> findByAccountId(String accountId);
}
