package com.carddemo.auth.controller;

import com.carddemo.auth.dto.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("AuthController Integration Tests")
class AuthControllerIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/v1/auth/login — valid ADMIN credentials returns 200 with token")
    void login_ValidAdminCredentials_Returns200() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setUserId("USR001");
        req.setPassword("Admin@123");

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.userId").value("USR001"))
                .andExpect(jsonPath("$.data.userType").value("ADMIN"))
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andExpect(jsonPath("$.data.landingPage").value("ADMIN_MENU"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/login — missing userId returns 400")
    void login_MissingUserId_Returns400() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setPassword("Admin@123");

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("POST /api/v1/auth/login — wrong password returns 401")
    void login_WrongPassword_Returns401() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setUserId("USR001");
        req.setPassword("WrongPassword");

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("POST /api/v1/auth/login — user not found returns 401")
    void login_UserNotFound_Returns401() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setUserId("NOTEXIST");
        req.setPassword("password");

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }
}
