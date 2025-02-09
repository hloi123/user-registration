package com.example.user_registration.controller;


import com.example.user_registration.dto.UserDto;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@AllArgsConstructor
public class AuthController {

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
    public String registrationForm(Model model) {
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
}
