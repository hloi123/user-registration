package com.example.user_registration.service.Impl;

import com.example.user_registration.dto.UserDto;
import com.example.user_registration.entity.Role;
import com.example.user_registration.entity.User;
import com.example.user_registration.repository.RoleRepository;
import com.example.user_registration.repository.UserRepository;
import com.example.user_registration.service.UserService;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String USER_ROLE = "ROLE_USER";
    private static final String ISO_DATE_FORMAT = "yyyy-MM-dd";

    public void createUser(UserDto userDto) {
        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        LocalDate date = LocalDate.parse(userDto.getDateOfBirth(), DateTimeFormatter.ofPattern(ISO_DATE_FORMAT));
        user.setDateOfBirth(date);
        Optional<Role> roleOptional = roleRepository.findByName(USER_ROLE);
        Role role = roleOptional.orElseGet(this::insertRole);
        user.setRoles(Set.of(role));
        userRepository.save(user);
    }

    public UserDto findUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        return !Objects.isNull(user) ? mapToUserDto(user) : null;
    }

    public void updateUser(UserDto updatedUserDto, Long userId) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        existingUser.setFirstName(updatedUserDto.getFirstName());
        existingUser.setLastName(updatedUserDto.getLastName());
        LocalDate date = LocalDate.parse(updatedUserDto.getDateOfBirth(), DateTimeFormatter.ofPattern(ISO_DATE_FORMAT));
        existingUser.setDateOfBirth(date);
        if (!StringUtils.isEmpty(updatedUserDto.getPassword())) {
            existingUser.setPassword(passwordEncoder.encode(updatedUserDto.getPassword()));
        }
        userRepository.save(existingUser);
    }


    private UserDto mapToUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setEmail(user.getEmail());
        userDto.setDateOfBirth(user.getDateOfBirth().toString());
        userDto.setRole(user.getRoles().stream().map(Role::getName).toList());
        return userDto;
    }

    private Role insertRole() {
        Role role = new Role();
        role.setName(USER_ROLE);
        return roleRepository.save(role);
    }

    /**
     * validate userDTO
     *
     * @param id
     * @param userDto
     * @param result
     */
    public void validateUserDto(Long id, UserDto userDto, BindingResult result) {
        // check password not empty when register new user
        if (Objects.isNull(id) && StringUtils.isEmpty(userDto.getPassword())) {
            result.rejectValue("password", "validation.password.required"
                    , "Password is required.");
        }

        // check password length
        if (!StringUtils.isEmpty(userDto.getPassword()) && userDto.getPassword().length() < 6) {
            result.rejectValue("password", "validation.password.min.length"
                    , "Password must be at least 6 characters.");
        }

        // check user is existed
        if (Objects.isNull(id)) {
            UserDto existingUser = findUserByEmail(userDto.getEmail());
            if (!Objects.isNull(existingUser) && !Objects.isNull(existingUser.getEmail())) {
                result.rejectValue("email", "validation.email.existed"
                        , "Email already registered.");
            }
        }

        // check dateOfbirth
        if (StringUtils.isNotBlank(userDto.getDateOfBirth())) {
            LocalDate inputDate = LocalDate.parse(userDto.getDateOfBirth(), DateTimeFormatter.ofPattern(ISO_DATE_FORMAT));
            if (LocalDate.now().isBefore(inputDate)) {
                result.rejectValue("dateOfBirth", "validation.dateOfBirth.futureDate"
                        , "Date of birth is in future");
            }
        }
    }
}