package com.team10.matchup;

import com.team10.matchup.user.User;
import com.team10.matchup.user.UserRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testUserSaveAndFind() {

        User user = new User();
        user.setUsername("testUser_" + System.currentTimeMillis());
        user.setPassword("1234");
        user.setName("테스트유저");
        user.setEmail("test@example.com");
        user.setPosition("FW");
        user.setBirth(LocalDate.of(2000, 1, 1));
        user.setCreatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        assertNotNull(savedUser.getId());
    }
}
