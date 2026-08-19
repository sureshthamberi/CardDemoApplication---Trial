package com.carddemo.auth.repository;

import com.carddemo.auth.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for user authentication and administration.
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {

    Optional<UserEntity> findByUserId(String userId);

    boolean existsByUserId(String userId);

    Page<UserEntity> findByUserIdGreaterThanEqualOrderByUserId(String startUserId, Pageable pageable);
}
