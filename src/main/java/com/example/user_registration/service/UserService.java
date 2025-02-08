package com.example.user_registration.service;

import com.example.user_registration.dto.UserDto;
import com.example.user_registration.entity.User;

public interface UserService {
    void createUser(UserDto userDto);

    User findUserByEmail(String email);

    UserDto findUserById(Long userId);

    void updateUser(UserDto updatedUserDto, Long userId);
}
