package com.carddemo.navigation.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/** Menu response payload for main/admin menus. */
@Getter
@Builder
public class MenuResponse {
    private final String     menuType;
    private final List<MenuItem> items;

    @Getter
    @Builder
    public static class MenuItem {
        private final String option;
        private final String code;
        private final String label;
    }
}
