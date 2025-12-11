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

    // 미래 시점의 매치 글(최신순)
    @Transactional(readOnly = true)
    public List<MatchPost> getAllMatchPosts() {
        LocalDateTime now = LocalDateTime.now();
        return matchPostRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .filter(post -> post.getMatchDatetime() != null && !post.getMatchDatetime().isBefore(now))
                .toList();
    }

    // 내가 요청한 매치 ID 목록
    @Transactional(readOnly = true)
    public List<Long> getRequestedMatchIdsForCurrentUser() {
        User user = currentUserService.getCurrentUser();

        return matchRequestRepository.findByRequesterUser_Id(user.getId())
                .stream()
                .map(req -> req.getMatchPost().getId())
                .collect(Collectors.toList());
    }

    // 내가 요청한 매치의 상태 map
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
        if (team == null) {
            throw new IllegalStateException("팀이 있어야 매치를 등록할 수 있습니다.");
        }
        if (date == null || time == null) {
            throw new IllegalArgumentException("경기 일자와 시간을 모두 입력해주세요.");
        }

        LocalDateTime matchAt = LocalDateTime.of(date, time);
        if (matchAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("과거 시점에는 매치를 등록할 수 없습니다.");
        }

        MatchPost post = new MatchPost();
        post.setTeam(team);
        post.setCreatedBy(user);
        post.setPlayerCount(playerCount);
        post.setLocation(location);
        post.setMatchDatetime(matchAt);
        post.setStatus("OPEN");

        matchPostRepository.save(post);
    }

    /* ===================== 매치 요청 ===================== */

    public void requestMatch(Long matchId) {

        User requester = currentUserService.getCurrentUser();
        Team requesterTeam = currentUserService.getCurrentUserTeamOrNull();

        if (requesterTeam == null) {
            throw new IllegalStateException("팀이 없는 사용자만 매치를 요청할 수 없습니다.");
        }

        MatchPost post = matchPostRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("매치를 찾을 수 없습니다."));

        if (!"OPEN".equals(post.getStatus())) {
            throw new IllegalStateException("신청할 수 없는 매치 상태입니다.");
        }
        if (post.getMatchDatetime() == null || post.getMatchDatetime().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("이미 지난 매치에는 신청할 수 없습니다.");
        }

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

        // 매치 글 작성자에게 알림 전송
        User host = post.getCreatedBy();
        if (host != null) {
            String content = requesterTeam.getName() + " 팀에서 매치 요청을 했습니다.";
            notificationService.send(host, "MATCH_REQUEST", content, req);
        }
    }

    /* ===================== 매치 삭제 ===================== */

    public void deleteMatch(Long matchId) {

        MatchPost post = matchPostRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("매치를 찾을 수 없습니다."));

        User current = currentUserService.getCurrentUser();

        if (!post.getCreatedBy().getId().equals(current.getId())) {
            throw new IllegalStateException("본인이 등록한 매치만 삭제할 수 있습니다.");
        }

        if ("MATCHED".equals(post.getStatus())) {
            throw new IllegalStateException("이미 매칭 완료된 매치는 삭제할 수 없습니다.");
        }

        matchPostRepository.delete(post);
    }

    /* ===================== 요청 수락 / 거절 ===================== */

    public void acceptRequest(Long matchRequestId) {
        MatchRequest req = matchRequestRepository.findById(matchRequestId)
                .orElseThrow(() -> new IllegalArgumentException("매치 요청을 찾을 수 없습니다."));
        acceptRequest(req);
    }

    public void acceptRequest(MatchRequest req) {

        if (!"PENDING".equals(req.getStatus())) {
            return;
        }

        Team opponentTeam = req.getRequesterTeam();
        if (opponentTeam == null) {
            throw new IllegalStateException("요청 팀 정보가 없습니다.");
        }

        MatchPost post = req.getMatchPost();

        req.setStatus("ACCEPTED");
        req.setRespondedAt(LocalDateTime.now());

        if (post != null) {
            post.setStatus("MATCHED");
            post.setMatchedTeam(opponentTeam);

            Team hostTeam = post.getTeam();
            LocalDateTime matchTime = post.getMatchDatetime();
            String place = post.getLocation();

            if (matchTime != null) {
                if (hostTeam != null) {
                    eventService.createMatchEvent(hostTeam, opponentTeam, matchTime, place);
                }
                if (opponentTeam != null) {
                    eventService.createMatchEvent(opponentTeam, hostTeam, matchTime, place);
                }
            }
        }

        User receiver = req.getRequesterUser();
        if (receiver != null) {
            String content = "매치 요청이 수락되었습니다.";
            notificationService.send(receiver, "MATCH_ACCEPTED", content, req);
        }
    }

    public void rejectRequest(Long matchRequestId) {
        MatchRequest req = matchRequestRepository.findById(matchRequestId)
                .orElseThrow(() -> new IllegalArgumentException("매치 요청을 찾을 수 없습니다."));
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
            String content = "매치 요청이 거절되었습니다.";
            notificationService.send(receiver, "MATCH_REJECTED", content, req);
        }
    }

    /* ===================== 메인화면용 조회 ===================== */

    // 내 팀의 가장 가까운 매칭 확정 경기 1개(status=MATCHED, 현재 시간 이후)
    @Transactional(readOnly = true)
    public MatchPost getNearestUpcomingMatch() {
        Team myTeam = currentUserService.getCurrentUserTeamOrNull();
        if (myTeam == null) return null;

        LocalDateTime now = LocalDateTime.now();

        List<MatchPost> created = matchPostRepository
                .findByTeamAndStatusAndMatchDatetimeAfterOrderByMatchDatetimeAsc(
                        myTeam, "MATCHED", now
                );

        List<MatchPost> accepted = matchPostRepository
                .findByMatchedTeamAndStatusAndMatchDatetimeAfterOrderByMatchDatetimeAsc(
                        myTeam, "MATCHED", now
                );

        created.addAll(accepted);

        return created.stream()
                .filter(m -> m.getMatchedTeam() != null) // 상대 팀 없는 OPEN 매치 제외
                .sorted(Comparator.comparing(MatchPost::getMatchDatetime))
                .findFirst()
                .orElse(null);
    }

    // 메인화면에 보여줄 "신청 가능" 매치 목록 (내가 올린 글/이미 요청한 글 제외)
    @Transactional(readOnly = true)
    public List<MatchPost> getAvailableMatchesForHome(int limit) {
        User me = currentUserService.getCurrentUser();
        Map<Long, String> myRequestMap = getMyRequestStatusMap();

        LocalDateTime now = LocalDateTime.now();

        return matchPostRepository
                .findByStatusAndMatchDatetimeAfterOrderByMatchDatetimeAsc(
                        "OPEN", now
                ).stream()
                .filter(post -> post.getMatchDatetime() != null)
                .filter(post -> post.getCreatedBy() == null ||
                        !post.getCreatedBy().getId().equals(me.getId())) // 내가 올린 글 제외
                .filter(post -> !myRequestMap.containsKey(post.getId()))   // 이미 요청한 글 제외
                .limit(limit)
                .collect(Collectors.toList());
    }

    /* ===================== 내 팀 예정 매치 조회 (upcoming 매치용) ===================== */

    @Transactional(readOnly = true)
    public List<MatchPost> getMyTeamUpcomingMatches() {

        Team myTeam = currentUserService.getCurrentUserTeamOrNull();
        if (myTeam == null) return List.of();

        List<MatchPost> created = matchPostRepository
                .findByTeamAndStatusAndMatchDatetimeAfterOrderByMatchDatetimeAsc(
                        myTeam, "MATCHED", LocalDateTime.now()
                );

        List<MatchPost> accepted = matchPostRepository
                .findByMatchedTeamAndStatusAndMatchDatetimeAfterOrderByMatchDatetimeAsc(
                        myTeam, "MATCHED", LocalDateTime.now()
                );

        created.addAll(accepted);
        return created.stream()
                .sorted(Comparator.comparing(MatchPost::getMatchDatetime))
                .collect(Collectors.toList());
    }
}
