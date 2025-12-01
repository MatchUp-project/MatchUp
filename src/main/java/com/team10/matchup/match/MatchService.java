package com.team10.matchup.match;

import com.team10.matchup.common.CurrentUserService;
import com.team10.matchup.team.Team;
import com.team10.matchup.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MatchService {

    private final MatchPostRepository matchPostRepository;
    private final MatchRequestRepository matchRequestRepository;
    private final CurrentUserService currentUserService;

    // 매치 글 등록
    public MatchPost createMatchPost(int playerCount,
                                     String location,
                                     LocalDate date,
                                     LocalTime time) {

        User currentUser = currentUserService.getCurrentUser();
        Team team = currentUserService.getCurrentUserTeamOrNull();

        if (team == null) {
            throw new IllegalStateException("사용자가 속한 팀이 없습니다.");
        }

        MatchPost post = new MatchPost();
        post.setTeam(team);
        post.setCreatedBy(currentUser);
        post.setPlayerCount(playerCount);
        post.setLocation(location);
        post.setMatchDatetime(LocalDateTime.of(date, time));
        post.setStatus("OPEN");

        return matchPostRepository.save(post);
    }

    // 전체 매치 목록 (최신순)
    @Transactional(readOnly = true)
    public List<MatchPost> getAllMatchPosts() {
        return matchPostRepository.findAllByOrderByCreatedAtDesc();
    }

    // 매치 신청
    public MatchRequest requestMatch(Long matchPostId) {
        User currentUser = currentUserService.getCurrentUser();
        Team requesterTeam = currentUserService.getCurrentUserTeamOrNull();

        if (requesterTeam == null) {
            throw new IllegalStateException("사용자가 속한 팀이 없습니다.");
        }

        MatchPost post = matchPostRepository.findById(matchPostId)
                .orElseThrow(() -> new IllegalArgumentException("매치 글을 찾을 수 없습니다."));

        // 자기 팀 매치에는 신청 못하게
        if (post.getTeam().getId().equals(requesterTeam.getId())) {
            throw new IllegalStateException("자신의 팀이 올린 매치에는 신청할 수 없습니다.");
        }

        // 이미 신청했는지 체크
        matchRequestRepository.findByMatchPost_IdAndRequesterUser_Id(matchPostId, currentUser.getId())
                .ifPresent(req -> {
                    throw new IllegalStateException("이미 이 매치에 신청했습니다.");
                });

        // 신청 생성
        MatchRequest request = new MatchRequest();
        request.setMatchPost(post);
        request.setRequesterTeam(requesterTeam);
        request.setRequesterUser(currentUser);

        // 여기서는 알림은 나중에 붙이고, 일단 신청만 저장
        return matchRequestRepository.save(request);
    }

    // (참고) 나중에 수락/거절 기능 붙일 때 사용할 수 있는 메서드 뼈대
    public void acceptRequest(Long requestId) {
        MatchRequest request = matchRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("매치 신청을 찾을 수 없습니다."));

        // 매치 상태 변경
        request.accept();
        MatchPost post = request.getMatchPost();
        post.setStatus("MATCHED");
        // 저장은 @Transactional 덕분에 자동 flush
    }

    public void rejectRequest(Long requestId) {
        MatchRequest request = matchRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("매치 신청을 찾을 수 없습니다."));

        request.reject();
    }
}
