package com.carddemo.navigation.service;

import com.carddemo.common.exception.BusinessRuleException;
import com.carddemo.navigation.dto.MenuResponse;
import com.carddemo.navigation.dto.MenuResponse.MenuItem;
import com.carddemo.navigation.dto.ValidateOptionResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Navigation service providing role-based menus and option validation.
 * LLD Section 4.4 — Navigation APIs.
 */
@Service
public class NavigationService {

    private static final List<MenuItem> MAIN_MENU_ITEMS = List.of(
            MenuItem.builder().option("1").code("ACCOUNT_INQUIRY").label("Account Inquiry").build(),
            MenuItem.builder().option("2").code("ACCOUNT_UPDATE").label("Account Update").build(),
            MenuItem.builder().option("3").code("BILL_PAYMENT").label("Bill Payment").build(),
            MenuItem.builder().option("4").code("CARD_SEARCH").label("Card Search").build(),
            MenuItem.builder().option("5").code("CARD_DETAIL").label("Card Detail").build(),
            MenuItem.builder().option("6").code("TRANSACTION_LIST").label("Transaction List").build(),
            MenuItem.builder().option("7").code("TRANSACTION_ADD").label("Add Transaction").build(),
            MenuItem.builder().option("8").code("PENDING_AUTH").label("Pending Authorizations").build(),
            MenuItem.builder().option("9").code("REPORT_REQUEST").label("Report Request").build(),
            MenuItem.builder().option("0").code("SIGN_OFF").label("Sign Off").build()
    );

    private static final List<MenuItem> ADMIN_MENU_ITEMS = List.of(
            MenuItem.builder().option("1").code("USER_LIST").label("User Administration").build(),
            MenuItem.builder().option("2").code("TXN_TYPE_LIST").label("Transaction Type Maintenance").build(),
            MenuItem.builder().option("3").code("ACCOUNT_INQUIRY").label("Account Inquiry").build(),
            MenuItem.builder().option("4").code("ACCOUNT_UPDATE").label("Account Update").build(),
            MenuItem.builder().option("5").code("CARD_SEARCH").label("Card Search").build(),
            MenuItem.builder().option("6").code("TRANSACTION_LIST").label("Transaction List").build(),
            MenuItem.builder().option("7").code("PENDING_AUTH").label("Pending Authorizations").build(),
            MenuItem.builder().option("8").code("REPORT_REQUEST").label("Report Request").build(),
            MenuItem.builder().option("0").code("SIGN_OFF").label("Sign Off").build()
    );

    private static final Map<String, String> MAIN_MENU_ROUTES = Map.of(
            "1", "/accounts/inquiry",
            "2", "/accounts/update",
            "3", "/payments/bill",
            "4", "/cards/search",
            "5", "/cards/detail",
            "6", "/transactions",
            "7", "/transactions/add",
            "8", "/pending-authorizations",
            "9", "/reports/requests"
    );

    private static final Map<String, String> ADMIN_MENU_ROUTES = Map.of(
            "1", "/admin/users",
            "2", "/admin/transaction-types",
            "3", "/accounts/inquiry",
            "4", "/accounts/update",
            "5", "/cards/search",
            "6", "/transactions",
            "7", "/pending-authorizations",
            "8", "/reports/requests"
    );

    /** Return the main menu for standard users. */
    public MenuResponse getMainMenu() {
        return MenuResponse.builder()
                .menuType("MAIN")
                .items(MAIN_MENU_ITEMS)
                .build();
    }

    /** Return the admin menu for admin users. */
    public MenuResponse getAdminMenu() {
        return MenuResponse.builder()
                .menuType("ADMIN")
                .items(ADMIN_MENU_ITEMS)
                .build();
    }

    /**
     * Validate a menu option for a given role.
     * LLD 4.4.3
     */
    public ValidateOptionResponse validateOption(String option, String userType) {
        boolean isAdmin = "ADMIN".equals(userType);
        List<MenuItem> items  = isAdmin ? ADMIN_MENU_ITEMS  : MAIN_MENU_ITEMS;
        Map<String, String> routes = isAdmin ? ADMIN_MENU_ROUTES : MAIN_MENU_ROUTES;

        MenuItem found = items.stream()
                .filter(i -> i.getOption().equals(option))
                .findFirst()
                .orElseThrow(() -> new BusinessRuleException("NAV-400-002", "Enter a valid option number"));

        String targetRoute = routes.getOrDefault(option, "/");

        return ValidateOptionResponse.builder()
                .valid(true)
                .targetPage(found.getCode())
                .targetRoute(targetRoute)
                .build();
    }
}
