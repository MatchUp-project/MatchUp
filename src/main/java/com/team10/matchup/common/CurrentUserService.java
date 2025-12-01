package com.team10.matchup.common;

import com.team10.matchup.team.Team;
import com.team10.matchup.team.TeamMember;
import com.team10.matchup.team.TeamMemberRepository;
import com.team10.matchup.user.User;
import com.team10.matchup.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrentUserService {

    private final UserRepository userRepository;
    private final TeamMemberRepository teamMemberRepository;

    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("로그인된 사용자를 찾을 수 없습니다."));
    }

    public Team getCurrentUserTeamOrNull() {
        User user = getCurrentUser();
        return teamMemberRepository.findFirstByUserId(user.getId())
                .map(TeamMember::getTeam)
                .orElse(null);
    }
}

