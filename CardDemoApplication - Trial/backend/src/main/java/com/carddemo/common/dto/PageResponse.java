package com.carddemo.common.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * Pagination wrapper included in paginated list responses.
 *
 * @param <T> item type
 */
@Getter
@Builder
public class PageResponse<T> {

    private final List<T> items;
    private final Pagination pagination;

    @Getter
    @Builder
    public static class Pagination {
        private final int     page;
        private final int     pageSize;
        private final boolean hasNext;
        private final boolean hasPrevious;
        private final Long    totalElements;
    }
}
