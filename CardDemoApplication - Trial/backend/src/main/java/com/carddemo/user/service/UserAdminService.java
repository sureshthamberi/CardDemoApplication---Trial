package com.carddemo.user.service;

import com.carddemo.auth.entity.UserEntity;
import com.carddemo.auth.repository.UserRepository;
import com.carddemo.common.dto.PageResponse;
import com.carddemo.common.dto.PageResponse.Pagination;
import com.carddemo.common.exception.BusinessRuleException;
import com.carddemo.common.exception.ConcurrencyConflictException;
import com.carddemo.common.exception.DuplicateResourceException;
import com.carddemo.common.exception.ResourceNotFoundException;
import com.carddemo.user.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * User administration service.
 * Implements LLD Section 2.4 and API Section 4.5.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserAdminService {

    private final UserRepository  userRepository;
    private final PasswordEncoder passwordEncoder;

    /** List users with pagination. LLD 4.5.1 */
    @Transactional(readOnly = true)
    public PageResponse<UserSummaryDto> listUsers(int page, int pageSize, String startUserId) {
        PageRequest pageable = PageRequest.of(page - 1, pageSize, Sort.by("userId").ascending());

        Page<UserEntity> entityPage;
        if (StringUtils.hasText(startUserId)) {
            entityPage = userRepository.findByUserIdGreaterThanEqualOrderByUserId(
                    startUserId.trim(), pageable);
        } else {
            entityPage = userRepository.findAll(pageable);
        }

        List<UserSummaryDto> items = entityPage.getContent().stream()
                .map(this::toSummary)
                .toList();

        return PageResponse.<UserSummaryDto>builder()
                .items(items)
                .pagination(Pagination.builder()
                        .page(page)
                        .pageSize(pageSize)
                        .hasNext(entityPage.hasNext())
                        .hasPrevious(entityPage.hasPrevious())
                        .totalElements(entityPage.getTotalElements())
                        .build())
                .build();
    }

    /** Get single user by ID. LLD 4.5.2 */
    @Transactional(readOnly = true)
    public UserDetailDto getUser(String userId) {
        UserEntity user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("USR-404-001", "User not found"));
        return toDetail(user);
    }

    /** Create a new user. LLD 4.5.3 */
    @Transactional
    public String createUser(CreateUserRequest request, String createdBy) {
        String userId = request.getUserId().trim();

        if (userRepository.existsByUserId(userId)) {
            throw new DuplicateResourceException("USR-409-001", "User already exists");
        }

        String hash = passwordEncoder.encode(request.getPassword());

        UserEntity entity = UserEntity.builder()
                .userId(userId)
                .firstName(request.getFirstName().trim())
                .lastName(request.getLastName().trim())
                .passwordHash(hash)
                .userType(request.getUserType())
                .status("ACTIVE")
                .createdBy(createdBy)
                .updatedBy(createdBy)
                .version(0L)
                .build();

        userRepository.save(entity);
        log.info("User created: {} by: {}", userId, createdBy);
        return userId;
    }

    /** Update an existing user. LLD 4.5.4 */
    @Transactional
    public long updateUser(String userId, UpdateUserRequest request, String updatedBy) {
        UserEntity existing = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("USR-404-001", "User not found"));

        // Optimistic locking check
        if (!Objects.equals(existing.getVersion(), request.getRowVersion())) {
            throw new ConcurrencyConflictException("USR-409-002", "Record changed by someone else");
        }

        // No-change detection
        String newFirstName = request.getFirstName().trim();
        String newLastName  = request.getLastName().trim();
        String newUserType  = request.getUserType();
        boolean passwordChanged = !passwordEncoder.matches(request.getPassword(), existing.getPasswordHash());

        if (existing.getFirstName().equals(newFirstName)
                && existing.getLastName().equals(newLastName)
                && existing.getUserType().equals(newUserType)
                && !passwordChanged) {
            throw new BusinessRuleException("USR-400-003", "Please modify to update");
        }

        existing.setFirstName(newFirstName);
        existing.setLastName(newLastName);
        existing.setUserType(newUserType);
        existing.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        existing.setUpdatedBy(updatedBy);
        existing.setUpdatedAt(LocalDateTime.now());

        UserEntity saved = userRepository.save(existing);
        log.info("User updated: {} by: {}", userId, updatedBy);
        return saved.getVersion();
    }

    /** Delete a user. LLD 4.5.5 */
    @Transactional
    public void deleteUser(String userId, String deletedBy) {
        UserEntity existing = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("USR-404-001", "User not found"));

        userRepository.delete(existing);
        log.info("User deleted: {} by: {}", userId, deletedBy);
    }

    // ------------------------------------------------------------------
    // Private mappers
    // ------------------------------------------------------------------

    private UserSummaryDto toSummary(UserEntity e) {
        return UserSummaryDto.builder()
                .userId(e.getUserId())
                .firstName(e.getFirstName())
                .lastName(e.getLastName())
                .userType(e.getUserType())
                .status(e.getStatus())
                .build();
    }

    private UserDetailDto toDetail(UserEntity e) {
        return UserDetailDto.builder()
                .userId(e.getUserId())
                .firstName(e.getFirstName())
                .lastName(e.getLastName())
                .userType(e.getUserType())
                .status(e.getStatus())
                .rowVersion(e.getVersion())
                .build();
    }
}
