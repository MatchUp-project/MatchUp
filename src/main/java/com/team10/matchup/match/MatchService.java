package com.team10.matchup.match;

import com.team10.matchup.common.CurrentUserService;
import com.team10.matchup.event.EventService;
import com.team10.matchup.notification.NotificationService;
import com.team10.matchup.team.Team;
import com.team10.matchup.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MatchService {

    private final MatchPostRepository matchPostRepository;
    private final MatchRequestRepository matchRequestRepository;
    private final CurrentUserService currentUserService;
    private final NotificationService notificationService;
    // ✅ 일정 자동 생성용
    private final EventService eventService;

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

    // 전체 매치 목록
    @Transactional(readOnly = true)
    public List<MatchPost> getAllMatchPosts() {
        return matchPostRepository.findAllByOrderByCreatedAtDesc();
    }

    // 현재 사용자가 이미 신청한 매치 id 목록
    @Transactional(readOnly = true)
    public List<Long> getRequestedMatchIdsForCurrentUser() {
        User currentUser = currentUserService.getCurrentUser();
        return matchRequestRepository.findByRequesterUser_Id(currentUser.getId())
                .stream()
                .map(req -> req.getMatchPost().getId())
                .collect(Collectors.toList());
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
        request = matchRequestRepository.save(request);

        // 🔔 글 작성자에게 알림 보내기
        User receiver = post.getCreatedBy();
        notificationService.send(
                receiver,
                "MATCH_REQUEST",
                requesterTeam.getName() + " 팀에서 매치 신청이 왔습니다.",
                request
        );

        return request;
    }

    // ✅ 수락 + 일정 자동 생성
    public void acceptRequest(Long requestId) {
        MatchRequest request = matchRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("매치 신청을 찾을 수 없습니다."));

        request.accept();
        MatchPost post = request.getMatchPost();
        post.setStatus("MATCHED");

        // 🔔 신청자에게 알림
        notificationService.send(
                request.getRequesterUser(),
                "MATCH_ACCEPTED",
                "매치 신청이 수락되었습니다.",
                request
        );

        // ⭐ 두 팀 일정 생성 (EventService 안에서 home/away 둘 다 저장)
        eventService.createMatchEvents(post, request.getRequesterTeam());
    }

    // 거절
    public void rejectRequest(Long requestId) {

        System.out.println("[MatchService] acceptRequest 호출, requestId = " + requestId);

        MatchRequest request = matchRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("매치 신청을 찾을 수 없습니다."));

        request.reject();

        // 🔔 신청자에게 알림
        notificationService.send(
                request.getRequesterUser(),
                "MATCH_REJECTED",
                "매치 신청이 거절되었습니다.",
                request
        );
    }
}
