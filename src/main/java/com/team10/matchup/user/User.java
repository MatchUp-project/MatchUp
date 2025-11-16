package com.team10.matchup.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "users")   // MySQL 테이블 이름
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // AUTO_INCREMENT 매핑
    private Long id;

    @Column(nullable = false, length = 50, unique = true)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(length = 50)
    private String name;

    @Column(length = 100)
    private String email;

    @Column(length = 30)
    private String position;

    // birth DATE
    private LocalDate birth;

    // created_at DATETIME
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
