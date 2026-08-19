package com.carddemo.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security configuration.
 * <ul>
 *   <li>Stateless JWT-based authentication</li>
 *   <li>Public paths: login, H2 console, Swagger</li>
 *   <li>Admin-only paths: /api/v1/admin/**</li>
 *   <li>Ops-only paths: /api/v1/ops/**</li>
 *   <li>All other paths require authentication</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider    tokenProvider;
    private final JwtAuthEntryPoint   authEntryPoint;
    private final JwtAccessDeniedHandler accessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(tokenProvider);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF — stateless API
            .csrf(AbstractHttpConfigurer::disable)

            // Allow H2 console frames
            .headers(headers -> headers.frameOptions(fo -> fo.sameOrigin()))

            // Stateless session management
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Exception handling
            .exceptionHandling(eh -> eh
                .authenticationEntryPoint(authEntryPoint)
                .accessDeniedHandler(accessDeniedHandler))

            // Authorization rules
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/api-docs/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()

                // Admin-only
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/v1/reference/transaction-types/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/v1/reference/transaction-types/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,  "/api/v1/reference/transaction-types/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE,"/api/v1/reference/transaction-types/**").hasRole("ADMIN")

                // Ops-only
                .requestMatchers("/api/v1/ops/**").hasAnyRole("ADMIN", "SYSTEM")

                // All other requests require authentication
                .anyRequest().authenticated())

            // Add JWT filter before UsernamePasswordAuthenticationFilter
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
