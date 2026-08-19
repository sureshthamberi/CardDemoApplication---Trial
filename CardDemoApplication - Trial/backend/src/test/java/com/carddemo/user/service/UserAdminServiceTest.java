package com.carddemo.user.service;

import com.carddemo.auth.entity.UserEntity;
import com.carddemo.auth.repository.UserRepository;
import com.carddemo.common.exception.BusinessRuleException;
import com.carddemo.common.exception.ConcurrencyConflictException;
import com.carddemo.common.exception.DuplicateResourceException;
import com.carddemo.common.exception.ResourceNotFoundException;
import com.carddemo.user.dto.CreateUserRequest;
import com.carddemo.user.dto.UpdateUserRequest;
import com.carddemo.user.dto.UserDetailDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserAdminService Unit Tests")
class UserAdminServiceTest {

    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @InjectMocks UserAdminService service;

    private UserEntity existingUser;

    @BeforeEach
    void setUp() {
        existingUser = UserEntity.builder()
                .userId("USR002")
                .firstName("Mary")
                .lastName("Smith")
                .passwordHash("$2a$10$hash")
                .userType("STANDARD")
                .status("ACTIVE")
                .version(2L)
                .build();
    }

    @Test
    @DisplayName("Get user — found returns detail DTO")
    void getUser_Found_ReturnsDetailDto() {
        when(userRepository.findByUserId("USR002")).thenReturn(Optional.of(existingUser));
        UserDetailDto detail = service.getUser("USR002");
        assertThat(detail.getUserId()).isEqualTo("USR002");
        assertThat(detail.getFirstName()).isEqualTo("Mary");
        assertThat(detail.getRowVersion()).isEqualTo(2L);
    }

    @Test
    @DisplayName("Get user — not found throws ResourceNotFoundException")
    void getUser_NotFound_ThrowsException() {
        when(userRepository.findByUserId(anyString())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getUser("NOTEXIST"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Create user — duplicate userId throws DuplicateResourceException")
    void createUser_Duplicate_ThrowsException() {
        when(userRepository.existsByUserId("USR002")).thenReturn(true);
        CreateUserRequest req = new CreateUserRequest();
        req.setUserId("USR002"); req.setFirstName("A"); req.setLastName("B");
        req.setPassword("p"); req.setUserType("STANDARD");
        assertThatThrownBy(() -> service.createUser(req, "ADMIN"))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("User already exists");
    }

    @Test
    @DisplayName("Create user — new user is saved and userId returned")
    void createUser_New_SavesAndReturnsId() {
        when(userRepository.existsByUserId("NEWUSER")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encoded");
        when(userRepository.save(any(UserEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        CreateUserRequest req = new CreateUserRequest();
        req.setUserId("NEWUSER"); req.setFirstName("New"); req.setLastName("User");
        req.setPassword("Secret@1"); req.setUserType("STANDARD");

        String id = service.createUser(req, "ADMIN");
        assertThat(id).isEqualTo("NEWUSER");
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("Update user — rowVersion mismatch throws ConcurrencyConflictException")
    void updateUser_VersionMismatch_ThrowsConflict() {
        when(userRepository.findByUserId("USR002")).thenReturn(Optional.of(existingUser));
        UpdateUserRequest req = new UpdateUserRequest();
        req.setFirstName("Mary"); req.setLastName("Smith"); req.setPassword("P");
        req.setUserType("STANDARD"); req.setRowVersion(99L); // mismatch

        assertThatThrownBy(() -> service.updateUser("USR002", req, "ADMIN"))
                .isInstanceOf(ConcurrencyConflictException.class)
                .hasMessageContaining("Record changed by someone else");
    }

    @Test
    @DisplayName("Update user — no-change detected throws BusinessRuleException")
    void updateUser_NoChange_ThrowsBusinessRuleException() {
        when(userRepository.findByUserId("USR002")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        UpdateUserRequest req = new UpdateUserRequest();
        req.setFirstName("Mary"); req.setLastName("Smith");
        req.setPassword("SamePassword"); req.setUserType("STANDARD"); req.setRowVersion(2L);

        assertThatThrownBy(() -> service.updateUser("USR002", req, "ADMIN"))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Please modify to update");
    }

    @Test
    @DisplayName("Delete user — not found throws ResourceNotFoundException")
    void deleteUser_NotFound_ThrowsException() {
        when(userRepository.findByUserId(anyString())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.deleteUser("MISSING", "ADMIN"))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
