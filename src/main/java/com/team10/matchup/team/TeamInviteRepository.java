package com.team10.matchup.team;

import com.team10.matchup.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamInviteRepository extends JpaRepository<TeamInvite, Long> {
    List<TeamInvite> findByTargetUserAndStatus(User targetUser, String status);
    Optional<TeamInvite> findByIdAndTargetUser(Long id, User targetUser);
}
