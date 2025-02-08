package com.example.user_registration.controller;


import com.example.user_registration.dto.UserDto;
import com.example.user_registration.entity.User;
import com.example.user_registration.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
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
public class AuthController {

    private final UserService userService;

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginForm() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            return "redirect:/user";
        }
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDto user = new UserDto();
        model.addAttribute("user", user);
        if (authentication != null
                && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            return "redirect:/user";
        } else {

            return "register";
        }
    }

    @PostMapping("/register/save")
    public String registration(
            @Valid @ModelAttribute("user") UserDto userDto,
            BindingResult result,
            Model model) {

        User existingUser = userService.findUserByEmail(userDto.getEmail());

        if (existingUser != null && existingUser.getEmail() != null && !existingUser.getEmail().isEmpty()) {
            result.rejectValue("email", null,
                    "There is already an account registered with the same email");
        }

        if (!userDto.getPassword().isEmpty()) {
            if (userDto.getPassword().length() < 6) {
                result.rejectValue("password", "field.min.length", "Password should have at least 6 characters");
            }
        } else {
            result.rejectValue("password", "field.min.length", "Password should not be empty.");
        }

        if (result.hasErrors()) {
            model.addAttribute("user", userDto);
            return "register";
        }

        userService.createUser(userDto);
        return "redirect:/register?success=true";
    }

    @GetMapping("/add")
    public String showUserAddForm(Model model) {
        UserDto user = new UserDto();
        model.addAttribute("user", user);
        return "add";
    }

    @GetMapping("/user")
    public String user(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findUserByEmail(username);
        model.addAttribute("user", user);
        return "user";
    }

    @GetMapping("/edit/{id}")
    public String updateUser(
            @PathVariable Long id,
            Model model) {
        UserDto user = userService.findUserById(id);
        model.addAttribute("user", user);
        return "edit";
    }

    @PostMapping("/edit/{id}")
    public String updateUserById(
            @Valid @ModelAttribute("user") UserDto updatedUserDto,
            BindingResult result,
            @PathVariable Long id,
            Model model) {

        if (!updatedUserDto.getPassword().isEmpty()) {
            if (updatedUserDto.getPassword().length() < 6) {
                result.rejectValue("password", "field.min.length", "Password should have at least 6 characters");
            }
        }

        if (result.hasErrors()) {
            model.addAttribute("user", updatedUserDto);
            return "edit";
        }

        userService.updateUser(updatedUserDto, id);
        return "redirect:/user";
    }

}
