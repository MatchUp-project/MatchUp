package com.team10.matchup.team;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    // 실제 JPA 쿼리 생성에 쓰이는 메서드
    Optional<TeamMember> findFirstByUser_Id(Long userId);

    // 🔹 PostService에서 쓰는 이름을 위한 alias
    default Optional<TeamMember> findFirstByUserId(Long userId) {
        return findFirstByUser_Id(userId);
    }

    List<TeamMember> findByTeam_Id(Long teamId);

    boolean existsByTeam_IdAndUser_Id(Long teamId, Long userId);
}
