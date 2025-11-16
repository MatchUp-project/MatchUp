package com.team10.matchup.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    // 기본 CRUD (findAll, findById, save, delete…) 전부 자동 제공
}
