package com.team10.matchup.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User signup(String username, String password, String name, String email) {

        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));   // 암호화된 비밀번호 저장
        user.setName(name);
        user.setEmail(email);
        user.setCreatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }
}
