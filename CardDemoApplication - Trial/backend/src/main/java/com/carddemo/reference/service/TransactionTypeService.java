package com.carddemo.reference.service;

import com.carddemo.common.dto.PageResponse;
import com.carddemo.common.dto.PageResponse.Pagination;
import com.carddemo.common.exception.BusinessRuleException;
import com.carddemo.common.exception.ConcurrencyConflictException;
import com.carddemo.common.exception.DuplicateResourceException;
import com.carddemo.common.exception.ResourceNotFoundException;
import com.carddemo.reference.dto.*;
import com.carddemo.reference.entity.TransactionTypeEntity;
import com.carddemo.reference.repository.TransactionTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionTypeService {

    private final TransactionTypeRepository repo;

    @Transactional(readOnly = true)
    public PageResponse<TransactionTypeDto> listTypes(String typeCode, String description, int page, int pageSize) {
        PageRequest pageable = PageRequest.of(page - 1, pageSize, Sort.by("typeCode").ascending());
        Page<TransactionTypeEntity> entityPage;
        if (StringUtils.hasText(typeCode)) {
            entityPage = repo.findByTypeCodeContainingIgnoreCase(typeCode, pageable);
        } else if (StringUtils.hasText(description)) {
            entityPage = repo.findByDescriptionContainingIgnoreCase(description, pageable);
        } else {
            entityPage = repo.findAll(pageable);
        }
        List<TransactionTypeDto> items = entityPage.getContent().stream().map(this::toDto).toList();
        return PageResponse.<TransactionTypeDto>builder()
                .items(items)
                .pagination(Pagination.builder()
                        .page(page).pageSize(pageSize)
                        .hasNext(entityPage.hasNext()).hasPrevious(entityPage.hasPrevious())
                        .totalElements(entityPage.getTotalElements()).build())
                .build();
    }

    @Transactional(readOnly = true)
    public TransactionTypeDto getType(String typeCode) {
        return toDto(repo.findByTypeCode(typeCode)
                .orElseThrow(() -> new ResourceNotFoundException("REF-404-001", "Transaction type not found")));
    }

    @Transactional
    public String createType(CreateTransactionTypeRequest req, String actor) {
        if (repo.existsByTypeCode(req.getTypeCode())) {
            throw new DuplicateResourceException("REF-409-001", "Transaction type already exists");
        }
        TransactionTypeEntity entity = TransactionTypeEntity.builder()
                .typeCode(req.getTypeCode().trim().toUpperCase())
                .description(req.getDescription().trim())
                .createdBy(actor)
                .updatedBy(actor)
                .version(0L)
                .build();
        repo.save(entity);
        log.info("Transaction type created: {} by: {}", req.getTypeCode(), actor);
        return req.getTypeCode();
    }

    @Transactional
    public long updateType(String typeCode, UpdateTransactionTypeRequest req, String actor) {
        TransactionTypeEntity existing = repo.findByTypeCode(typeCode)
                .orElseThrow(() -> new ResourceNotFoundException("REF-404-001", "Transaction type not found"));
        if (!Objects.equals(existing.getVersion(), req.getRowVersion())) {
            throw new ConcurrencyConflictException("REF-409-002", "Record changed by someone else");
        }
        if (existing.getDescription().equals(req.getDescription().trim())) {
            throw new BusinessRuleException("REF-400-001", "No change detected");
        }
        existing.setDescription(req.getDescription().trim());
        existing.setUpdatedBy(actor);
        existing.setUpdatedAt(LocalDateTime.now());
        TransactionTypeEntity saved = repo.save(existing);
        return saved.getVersion();
    }

    @Transactional
    public void deleteType(String typeCode, String actor) {
        TransactionTypeEntity existing = repo.findByTypeCode(typeCode)
                .orElseThrow(() -> new ResourceNotFoundException("REF-404-001", "Transaction type not found"));
        repo.delete(existing);
        log.info("Transaction type deleted: {} by: {}", typeCode, actor);
    }

    private TransactionTypeDto toDto(TransactionTypeEntity e) {
        return TransactionTypeDto.builder()
                .typeCode(e.getTypeCode())
                .description(e.getDescription())
                .rowVersion(e.getVersion())
                .build();
    }
}
