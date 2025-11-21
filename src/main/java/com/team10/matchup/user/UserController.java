package com.team10.matchup.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

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

        // ★ 아이디 최소 길이 검사
        if (username.length() < 4) {
            model.addAttribute("errorMessage", "아이디는 최소 4자 이상이어야 합니다.");
            return "signup";
        }

        // ★ 비밀번호 최소 길이 검사
        if (password.length() < 6) {
            model.addAttribute("errorMessage", "비밀번호는 최소 6자 이상이어야 합니다.");
            return "signup";
        }

        // ★ 비밀번호 불일치 검사
        if (!password.equals(passwordConfirm)) {
            model.addAttribute("errorMessage", "비밀번호가 일치하지 않습니다.");
            return "signup";
        }

        // ★ 이미 존재하는 아이디 검사
        if (userService.isUsernameExists(username)) {
            model.addAttribute("errorMessage", "이미 사용 중인 아이디입니다.");
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
        return "login";   // login.html 보여줌
    }

    @GetMapping("/profile")
    public String userProfile(Model model) {
        User user = userService.getCurrentUser();
        model.addAttribute("user", user);
        return "profile";
    }

    @GetMapping("/profile/edit")
    public String editProfile(Model model) {
        User user = userService.getCurrentUser();
        model.addAttribute("user", user);
        return "profile_edit";
    }

    @PostMapping("/profile/edit")
    public String updateProfile(@RequestParam String name,
                                @RequestParam String email,
                                @RequestParam String position,
                                @RequestParam(required = false) String birth,
                                Model model) {

        User currentUser = userService.getCurrentUser();

        LocalDate birthDate = null;
        if (birth != null && !birth.isEmpty()) {
            birthDate = LocalDate.parse(birth);
        }

        userService.updateProfile(
                currentUser.getUsername(),
                name,
                email,
                position,
                birthDate
        );

        return "redirect:/profile";
    }

}
