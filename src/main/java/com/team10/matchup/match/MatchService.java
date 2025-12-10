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
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MatchService {

    private final MatchPostRepository matchPostRepository;
    private final MatchRequestRepository matchRequestRepository;
    private final CurrentUserService currentUserService;
    private final NotificationService notificationService;
    private final EventService eventService;

    /* ===================== 조회 ===================== */

    // 전체 매치 가져오기 (등록 최신순)
    @Transactional(readOnly = true)
    public List<MatchPost> getAllMatchPosts() {
        return matchPostRepository.findAllByOrderByCreatedAtDesc();
    }

    // 내가 신청한 매치 ID 목록
    @Transactional(readOnly = true)
    public List<Long> getRequestedMatchIdsForCurrentUser() {
        User user = currentUserService.getCurrentUser();

        return matchRequestRepository.findByRequesterUser_Id(user.getId())
                .stream()
                .map(req -> req.getMatchPost().getId())
                .collect(Collectors.toList());
    }

    // 내가 신청한 매치의 [matchPostId -> status] 맵
    @Transactional(readOnly = true)
    public Map<Long, String> getMyRequestStatusMap() {
        User user = currentUserService.getCurrentUser();

        return matchRequestRepository.findByRequesterUser_Id(user.getId())
                .stream()
                .collect(Collectors.toMap(
                        req -> req.getMatchPost().getId(),
                        MatchRequest::getStatus,
                        (oldVal, newVal) -> newVal
                ));
    }

    /* ===================== 매치 생성 ===================== */

    public void createMatchPost(int playerCount, String location,
                                LocalDate date, LocalTime time) {

        User user = currentUserService.getCurrentUser();
        Team team = currentUserService.getCurrentUserTeamOrNull();

        MatchPost post = new MatchPost();
        post.setTeam(team);
        post.setCreatedBy(user);
        post.setPlayerCount(playerCount);
        post.setLocation(location);
        post.setMatchDatetime(LocalDateTime.of(date, time));
        post.setStatus("OPEN");

        matchPostRepository.save(post);
    }

    /* ===================== 매치 신청 ===================== */

    public void requestMatch(Long matchId) {

        User requester = currentUserService.getCurrentUser();
        Team requesterTeam = currentUserService.getCurrentUserTeamOrNull();

        if (requesterTeam == null) {
            throw new IllegalStateException("팀이 없는 사용자는 매치를 신청할 수 없습니다.");
        }

        MatchPost post = matchPostRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("매치를 찾을 수 없습니다."));

        boolean exists = matchRequestRepository
                .findByMatchPost_IdAndRequesterUser_Id(matchId, requester.getId())
                .isPresent();

        if (exists) {
            return;
        }

        MatchRequest req = new MatchRequest();
        req.setMatchPost(post);
        req.setRequesterUser(requester);
        req.setRequesterTeam(requesterTeam);
        req.setStatus("PENDING");

        matchRequestRepository.save(req);

        // 매치 글 작성자(호스트)에게 알림
        User host = post.getCreatedBy();
        if (host != null) {
            String content = requesterTeam.getName() + " 팀에서 매치 신청이 왔습니다.";
            notificationService.send(host, "MATCH_REQUEST", content, req);
        }
    }

    /* ===================== 매치 삭제 ===================== */

    public void deleteMatch(Long matchId) {

        MatchPost post = matchPostRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("매치를 찾을 수 없습니다."));

        User current = currentUserService.getCurrentUser();

        if (!post.getCreatedBy().getId().equals(current.getId())) {
            throw new IllegalStateException("내가 등록한 매치만 삭제할 수 있습니다.");
        }

        if ("MATCHED".equals(post.getStatus())) {
            throw new IllegalStateException("이미 매치 완료된 매치는 삭제할 수 없습니다.");
        }

        matchPostRepository.delete(post);
    }

    /* ===================== 신청 수락 / 거절 ===================== */

    public void acceptRequest(Long matchRequestId) {
        MatchRequest req = matchRequestRepository.findById(matchRequestId)
                .orElseThrow(() -> new IllegalArgumentException("매치 신청을 찾을 수 없습니다."));
        acceptRequest(req);
    }

    public void acceptRequest(MatchRequest req) {

        if (!"PENDING".equals(req.getStatus())) {
            return;
        }

        req.setStatus("ACCEPTED");
        req.setRespondedAt(LocalDateTime.now());

        MatchPost post = req.getMatchPost();
        if (post != null) {
            post.setStatus("MATCHED");

            Team hostTeam = post.getTeam();             // 매치 글 올린 팀
            Team awayTeam = req.getRequesterTeam();     // 신청한 팀
            LocalDateTime matchTime = post.getMatchDatetime();
            String place = post.getLocation();

            if (matchTime != null) {
                if (hostTeam != null) {
                    eventService.createMatchEvent(hostTeam, matchTime, place);
                }
                if (awayTeam != null) {
                    eventService.createMatchEvent(awayTeam, matchTime, place);
                }
            }
        }

        User receiver = req.getRequesterUser();
        if (receiver != null) {
            String content = "매치 신청이 수락되었습니다.";
            notificationService.send(receiver, "MATCH_ACCEPTED", content, req);
        }
    }

    public void rejectRequest(Long matchRequestId) {
        MatchRequest req = matchRequestRepository.findById(matchRequestId)
                .orElseThrow(() -> new IllegalArgumentException("매치 신청을 찾을 수 없습니다."));
        rejectRequest(req);
    }

    public void rejectRequest(MatchRequest req) {

        if (!"PENDING".equals(req.getStatus())) {
            return;
        }

        req.setStatus("REJECTED");
        req.setRespondedAt(LocalDateTime.now());

        User receiver = req.getRequesterUser();
        if (receiver != null) {
            String content = "매치 신청이 거절되었습니다.";
            notificationService.send(receiver, "MATCH_REJECTED", content, req);
        }
    }

    /* ===================== 홈 화면용 조회 ===================== */

    // 가장 가까운 예정 매치 1개 (status=OPEN, 현재 시간 이후)
    @Transactional(readOnly = true)
    public MatchPost getNearestUpcomingMatch() {
        LocalDateTime now = LocalDateTime.now();

        return matchPostRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .filter(post ->
                        post.getMatchDatetime() != null &&
                                "OPEN".equals(post.getStatus()) &&
                                !post.getMatchDatetime().isBefore(now)
                )
                .sorted(Comparator.comparing(MatchPost::getMatchDatetime))
                .findFirst()
                .orElse(null);
    }

    // 홈 화면에 보여줄 매치 목록 (limit 개수만큼)
    @Transactional(readOnly = true)
    public List<MatchPost> getAvailableMatchesForHome(int limit) {
        LocalDateTime now = LocalDateTime.now();

        return matchPostRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .filter(post ->
                        post.getMatchDatetime() != null &&
                                "OPEN".equals(post.getStatus()) &&
                                !post.getMatchDatetime().isBefore(now)
                )
                .sorted(Comparator.comparing(MatchPost::getMatchDatetime))
                .limit(limit)
                .collect(Collectors.toList());
    }

    /* ===================== 내 팀 예정 매치 조회 (upcoming 페이지용) ===================== */

    @Transactional(readOnly = true)
    public List<MatchPost> getMyTeamUpcomingMatches() {

        Team myTeam = currentUserService.getCurrentUserTeamOrNull();
        if (myTeam == null) {
            return List.of();
        }

        LocalDateTime now = LocalDateTime.now();

        return matchPostRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .filter(post ->
                        post.getTeam() != null &&
                                post.getTeam().getId().equals(myTeam.getId()) &&
                                post.getMatchDatetime() != null &&
                                !post.getMatchDatetime().isBefore(now)
                )
                .sorted(Comparator.comparing(MatchPost::getMatchDatetime))
                .collect(Collectors.toList());
    }
}
