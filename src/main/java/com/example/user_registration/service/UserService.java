package com.example.user_registration.service;

import com.example.user_registration.dto.UserDto;
import org.springframework.validation.BindingResult;

public interface UserService {
    void createUser(UserDto userDto);

    UserDto findUserByEmail(String email);

    void updateUser(UserDto updatedUserDto, Long userId);

    void validateUserDto(Long id, UserDto userDto, BindingResult result);
}
