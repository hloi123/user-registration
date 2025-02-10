package com.example.user_registration.controller;

import com.example.user_registration.dto.UserDto;
import com.example.user_registration.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@AllArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user")
    public String getUser(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDto user = userService.findUserByEmail(username);
        model.addAttribute("user", user);
        return "user";
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user/edit")
    public String updateUserForm(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDto user = userService.findUserByEmail(username);
        model.addAttribute("user", user);
        return "edit";
    }

    @PreAuthorize("hasRole('USER') and #updatedUserDto.email == authentication.name")
    @PostMapping("/user/edit/{id}")
    public String updateUser(
            @Valid @ModelAttribute("user") UserDto updatedUserDto,
            BindingResult result,
            @PathVariable Long id,
            Model model) {

        userService.validateUserDto(id, updatedUserDto, result);

        if (result.hasErrors()) {
            model.addAttribute("user", updatedUserDto);
            return "edit";
        }
        userService.updateUser(updatedUserDto, id);
        return "redirect:/user";
    }

    @PostMapping("/register")
    public String registerUser(
            @Valid @ModelAttribute("user") UserDto userDto,
            BindingResult result,
            Model model) {

        userService.validateUserDto(null, userDto, result);

        if (result.hasErrors()) {
            model.addAttribute("user", userDto);
            return "register";
        }
        userService.createUser(userDto);
        return "redirect:/register?success=true";
    }
}
