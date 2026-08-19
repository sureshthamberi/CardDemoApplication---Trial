package com.carddemo.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI / Swagger configuration.
 *
 * Registers a "BearerAuth" HTTP security scheme so that the Swagger UI
 * shows the 🔒 Authorize button and attaches the JWT token automatically
 * to every protected request.
 *
 * Usage in Swagger UI:
 *   1. POST /api/v1/auth/login  →  copy the returned "token" value
 *   2. Click "Authorize"  →  enter:  Bearer <token>
 *   3. All subsequent requests will include the Authorization header.
 */
@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "BearerAuth";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CardDemo API")
                        .description("""
                                CardDemo Application REST API.
                                
                                **Authentication:**
                                1. Use `POST /api/v1/auth/login` with `userId` + `password` to obtain a JWT token.
                                2. Click the **Authorize** button (🔒) above.
                                3. Enter `Bearer <your-token>` and click **Authorize**.
                                4. All protected endpoints will now include your token automatically.
                                
                                **Test credentials:**
                                | User | Password | Role |
                                |------|----------|------|
                                | USR001 | Admin@123 | ADMIN |
                                | USR002 | User@123  | STANDARD |
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("CardDemo Team")
                                .email("support@carddemo.example.com")))
                // Register the Bearer token scheme
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Paste your JWT token here (without the 'Bearer ' prefix — Swagger adds it automatically)")))
                // Apply the scheme globally to all operations
                .addSecurityItem(new SecurityRequirement()
                        .addList(SECURITY_SCHEME_NAME));
    }
}
