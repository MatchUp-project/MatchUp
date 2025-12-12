package com.team10.matchup.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final UserRepository userRepository;
    private final UserService userService;

    @GetMapping("/profile")
    public String profile(Model model) {
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
                                @RequestParam(required = false) String position,
                                @RequestParam(required = false) String birth) {

        User currentUser = userService.getCurrentUser();

        if (birth != null && !birth.isEmpty()) {
            currentUser.setBirth(LocalDate.parse(birth));
        }

        currentUser.setName(name);
        currentUser.setEmail(email);
        if (position != null && !position.isBlank()) {
            String normalized = position.toUpperCase();
            if (!com.team10.matchup.common.PositionCategory.isValid(normalized)) {
                throw new IllegalArgumentException("유효한 포지션(FW, MF, DF, GK)이 아닙니다.");
            }
            currentUser.setPosition(normalized);
        } else {
            currentUser.setPosition(null);
        }

        userRepository.save(currentUser);

        return "redirect:/profile";
    }
}
