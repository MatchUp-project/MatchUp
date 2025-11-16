package com.team10.matchup.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/signup")
    public String signupForm() {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@RequestParam String username,
                         @RequestParam String password,
                         @RequestParam String passwordConfirm,
                         @RequestParam String name,
                         @RequestParam String email,
                         Model model) {

        // 비밀번호 확인
        if (!password.equals(passwordConfirm)) {
            model.addAttribute("errorMessage", "비밀번호가 일치하지 않습니다.");
            return "signup";
        }

        try {
            userService.signup(username, password, name, email);
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "signup";
        }

        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }
}
