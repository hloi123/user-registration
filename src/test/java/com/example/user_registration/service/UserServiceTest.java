package com.example.user_registration.service;

import com.example.user_registration.dto.UserDto;
import com.example.user_registration.entity.Role;
import com.example.user_registration.entity.User;
import com.example.user_registration.repository.RoleRepository;
import com.example.user_registration.repository.UserRepository;
import com.example.user_registration.service.Impl.UserServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;
    @Mock
    RoleRepository roleRepository;
    @Mock
    PasswordEncoder passwordEncoder;

    @Captor
    ArgumentCaptor<User> userCaptor;

    private static final String PASSWORD = "123456";
    private static final String HASH_PASSWORD = "$2a$10$joKTKcm/lZnxdGmiVm4FautpgVdFaPLLmoUEkN.KWCDj8o/rAroy2";
    private static final String USER_ROLE = "ROLE_USER";

    private UserDto userDto;
    private Role role;
    private User user;

    @BeforeEach
    void initData() {
        userDto = new UserDto();
        userDto.setFirstName("Peter");
        userDto.setLastName("Parker");
        userDto.setEmail("abc@gmail.com");
        userDto.setPassword(PASSWORD);
        userDto.setDateOfBirth("2025-02-01");

        role = new Role();
        role.setName(USER_ROLE);
        role.setId(1L);

        user = new User();
        user.setId(1L);
        user.setFirstName("Peter");
        user.setLastName("Parker");
        user.setEmail("abc@gmail.com");
        user.setPassword(HASH_PASSWORD);
        user.setDateOfBirth(LocalDate.parse("2025-02-01"));
    }

    @Test
    void testCreateUser_validDto_success() {
        // given
        when(passwordEncoder.encode(any())).thenReturn(HASH_PASSWORD);
        when(roleRepository.findByName(USER_ROLE)).thenReturn(Optional.of(role));

        // when
        userService.createUser(userDto);

        // then
        verify(userRepository).save(userCaptor.capture());
        User newUser = userCaptor.getValue();
        assertEquals(userDto.getEmail(), newUser.getEmail());
        assertEquals(userDto.getFirstName(), newUser.getFirstName());
        assertEquals(userDto.getLastName(), newUser.getLastName());
        assertEquals(userDto.getDateOfBirth(), newUser.getDateOfBirth().toString());
    }

    @Test
    void testUpdateUser_invalidId_throwException() {
        // given
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        // when
        var exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> userService.updateUser(userDto, 1L));

        // Then
        Assertions.assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testUpdateUser_withId_returnSuccess() {
        // given
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(any())).thenReturn(HASH_PASSWORD);
        userDto.setFirstName("Robert");
        userDto.setLastName("Shu");
        // when
        userService.updateUser(userDto, 1L);

        // Then
        verify(userRepository).save(userCaptor.capture());
        User updateUser = userCaptor.getValue();
        assertEquals(userDto.getEmail(), updateUser.getEmail());
        assertEquals(userDto.getFirstName(), updateUser.getFirstName());
        assertEquals(userDto.getLastName(), updateUser.getLastName());
        assertEquals(userDto.getDateOfBirth(), updateUser.getDateOfBirth().toString());
    }

}
