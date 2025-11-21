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

    // ★ 중복 아이디 확인용 메서드 추가
    public boolean isUsernameExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    // 기존 signup 메서드 수정
    public void signup(String username, String password, String name, String email) {

        // 여기서도 한번 더 체크 (보안상)
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("이미 존재하는 아이디입니다.");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password)); // 비밀번호 암호화
        user.setName(name);
        user.setEmail(email);
        userRepository.save(user);
    }
}
