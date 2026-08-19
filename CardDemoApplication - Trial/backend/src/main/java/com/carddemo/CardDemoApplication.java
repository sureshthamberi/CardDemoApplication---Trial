package com.carddemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * CardDemo Application — Spring Boot Entry Point.
 * <p>
 * Provides all business services for the CardDemo system including:
 * authentication, user administration, account management, card management,
 * transaction management, pending authorizations, fraud management,
 * report requests, and internal/operational services.
 * </p>
 */
@SpringBootApplication
public class CardDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(CardDemoApplication.class, args);
    }
}
