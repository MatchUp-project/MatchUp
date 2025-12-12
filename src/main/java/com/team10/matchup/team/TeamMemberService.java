package com.team10.matchup.team;

import com.team10.matchup.user.User;
import com.team10.matchup.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamMemberService {

    private final TeamMemberRepository teamMemberRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;

    // 로그인한 유저의 팀 멤버 정보
    @Transactional(readOnly = true)
    public Optional<TeamMember> findMyTeamMember(Long userId) {
        return teamMemberRepository.findFirstByUser_Id(userId);
    }

    // 팀에 속한 모든 멤버 조회
    @Transactional(readOnly = true)
    public List<TeamMember> getTeamMembers(Long teamId) {
        return teamMemberRepository.findByTeam_Id(teamId);
    }

    // 멤버 추가 (팀장만 가능)
    public void addMember(Long leaderUserId, String usernameToAdd) {
        // 리더(요청 보낸 사람) 정보
        TeamMember leaderMember = teamMemberRepository.findFirstByUser_Id(leaderUserId)
                .orElseThrow(() -> new IllegalArgumentException("소속 팀이 없습니다."));

        if (leaderMember.getRole() != TeamMember.Role.LEADER) {
            throw new IllegalStateException("멤버를 추가할 권한이 없습니다.");
        }

        Team team = leaderMember.getTeam();

        // 추가할 유저 찾기
        User userToAdd = userRepository.findByUsername(usernameToAdd)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));

        // 이미 이 팀에 있는지 체크
        boolean alreadyInTeam =
                teamMemberRepository.existsByTeam_IdAndUser_Id(team.getId(), userToAdd.getId());

        if (alreadyInTeam) {
            throw new IllegalStateException("이미 팀에 속한 멤버입니다.");
        }

        TeamMember newMember = TeamMember.builder()
                .team(team)
                .user(userToAdd)
                .role(TeamMember.Role.PLAYER)
                .build();

        teamMemberRepository.save(newMember);
    }

    // 멤버 삭제 (팀장만 가능)
    public void removeMember(Long leaderUserId, Long teamMemberId) {

        TeamMember leaderMember = teamMemberRepository.findFirstByUser_Id(leaderUserId)
                .orElseThrow(() -> new IllegalArgumentException("소속 팀이 없습니다."));

        if (leaderMember.getRole() != TeamMember.Role.LEADER) {
            throw new IllegalStateException("멤버를 삭제할 권한이 없습니다.");
        }

        TeamMember target = teamMemberRepository.findById(teamMemberId)
                .orElseThrow(() -> new IllegalArgumentException("멤버를 찾을 수 없습니다."));

        // 같은 팀인지 확인
        if (!target.getTeam().getId().equals(leaderMember.getTeam().getId())) {
            throw new IllegalStateException("같은 팀의 멤버만 삭제할 수 있습니다.");
        }

        // 팀장이 자기 자신을 삭제 못 하게 막기 (원하면 바꿔도 됨)
        if (target.getUser().getId().equals(leaderUserId)
                && target.getRole() == TeamMember.Role.LEADER) {
            throw new IllegalStateException("팀장은 스스로를 삭제할 수 없습니다.");
        }

        teamMemberRepository.delete(target);
    }

    // 일반 멤버가 스스로 팀 탈퇴
    public void leaveTeam(Long userId) {
        TeamMember me = teamMemberRepository.findFirstByUser_Id(userId)
                .orElseThrow(() -> new IllegalStateException("소속 팀이 없습니다."));

        if (me.getRole() == TeamMember.Role.LEADER) {
            throw new IllegalStateException("팀장은 탈퇴 전에 팀장을 위임해야 합니다.");
        }

        teamMemberRepository.delete(me);
    }
}
