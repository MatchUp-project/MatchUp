package com.team10.matchup.match;

import com.team10.matchup.common.CurrentUserService;
import com.team10.matchup.notification.NotificationRepository;
import com.team10.matchup.notification.NotificationService;
import com.team10.matchup.notification.NotificationType;
import com.team10.matchup.team.Team;
import com.team10.matchup.user.User;
import com.team10.matchup.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Comparator;


@Service
@RequiredArgsConstructor
@Transactional
public class MatchService {

    private final MatchPostRepository matchPostRepository;
    private final MatchRequestRepository matchRequestRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final CurrentUserService currentUserService;
    private final NotificationRepository notificationRepository;


    /* ===================== 조회 ===================== */

    // 전체 매치 가져오기
    @Transactional(readOnly = true)
    public List<MatchPost> getAllMatchPosts() {
        return getAllMatchPosts(null);
    }

    @Transactional(readOnly = true)
    public List<MatchPost> getAllMatchPosts(String region) {
        if (region == null || region.isBlank()) {
            return matchPostRepository.findAllByOrderByCreatedAtDesc();
        }
        return matchPostRepository.findAllByRegionOrderByCreatedAtDesc(region);
    }

    // (예전) 내가 신청한 매치 ID 목록 – 안 써도 되지만 놔둬도 됨
    @Transactional(readOnly = true)
    public List<Long> getRequestedMatchIdsForCurrentUser() {
        User user = currentUserService.getCurrentUser();

        return matchRequestRepository.findByRequesterUser_Id(user.getId())
                .stream()
                .map(req -> req.getMatchPost().getId())
                .collect(Collectors.toList());
    }

    // ✅ 새로 추가: 내가 신청한 매치의 [matchPostId -> status] 맵
    @Transactional(readOnly = true)
    public Map<Long, String> getMyRequestStatusMap() {
        User user = currentUserService.getCurrentUser();

        return matchRequestRepository.findByRequesterUser_Id(user.getId())
                .stream()
                .collect(Collectors.toMap(
                        req -> req.getMatchPost().getId(),
                        MatchRequest::getStatus,
                        (oldVal, newVal) -> newVal   // 혹시 중복 있으면 마지막 값 사용
                ));
    }

    /* ===================== 매치 생성 ===================== */

    public void createMatchPost(int playerCount, String location, String region,
                                LocalDate date, LocalTime time) {

        User user = currentUserService.getCurrentUser();
        Team team = currentUserService.getCurrentUserTeamOrNull();

        MatchPost post = new MatchPost();
        post.setTeam(team);
        post.setCreatedBy(user);
        post.setPlayerCount(playerCount);
        post.setLocation(location);
        post.setRegion(region);
        post.setMatchDatetime(LocalDateTime.of(date, time));
        post.setStatus("OPEN");

        matchPostRepository.save(post);
    }

    /* ===================== 매치 신청 ===================== */

    public void requestMatch(Long matchId) {

        User requester = currentUserService.getCurrentUser();

        // 🔥 신청자의 팀 가져오기 (null이면 신청 불가능)
        Team requesterTeam = currentUserService.getCurrentUserTeamOrNull();
        if (requesterTeam == null) {
            throw new IllegalStateException("팀에 소속된 사용자만 매치를 신청할 수 있습니다.");
        }

        MatchPost post = matchPostRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("매치를 찾을 수 없습니다."));

        boolean exists = matchRequestRepository
                .findByMatchPost_IdAndRequesterUser_Id(matchId, requester.getId())
                .isPresent();

        if (exists) {
            return; // 이미 신청함 → 아무 동작 안 하고 끝
        }

        // 🔥 신규 신청 생성 (너의 기존 코드 유지)
        MatchRequest req = new MatchRequest();
        req.setMatchPost(post);
        req.setRequesterUser(requester);
        req.setRequesterTeam(requesterTeam);
        req.setStatus("PENDING");

        matchRequestRepository.save(req); // 저장

        // ==========================================================
        // ⭐ 추가된 부분: 매치 생성자의 "팀장"에게 알림 보내기
        // ==========================================================

        // (1) 매치 글 작성자의 팀장 ID 가져오기
        Long leaderId = post.getTeam().getLeaderId();

        // (2) 팀장 유저 찾기
        User leader = userRepository.findById(leaderId)
                .orElseThrow(() -> new IllegalArgumentException("팀장을 찾을 수 없습니다."));

        // (3) 알림 발송
        notificationService.send(
                leader,
                NotificationType.MATCH_REQUEST.name(),
                (requesterTeam != null ? requesterTeam.getName() : requester.getName()) + " 팀이 매치를 신청했습니다.",
                req
        );
    }


    /* ===================== 매치 삭제 ===================== */

    @Transactional
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

        // 🔥 1) 이 매치의 모든 MatchRequest 조회
        List<MatchRequest> requests = matchRequestRepository.findByMatchPostId(matchId);

        for (MatchRequest req : requests) {
            // 🔥 1-1) 이 요청과 연결된 Notification 먼저 삭제
            notificationRepository.deleteAll(
                    notificationRepository.findByRelatedMatchRequest(req)
            );
        }

        // 🔥 2) match_request 전부 삭제
        matchRequestRepository.deleteAll(requests);

        // 🔥 3) 마지막으로 매치 삭제
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

        // 1) 신청자 팀 가져오기
        Team opponentTeam = req.getRequesterTeam();
        if (opponentTeam == null) {
            throw new IllegalStateException("신청자의 팀 정보가 없습니다.");
        }

        // 2) 매치 정보 가져오기
        MatchPost post = req.getMatchPost();

        // 3) MatchRequest 상태 변경
        req.setStatus("ACCEPTED");
        req.setRespondedAt(LocalDateTime.now());

        // 4) 매치 상태를 MATCHED로 변경
        post.setStatus("MATCHED");

        // 5) 상대팀 세팅 (중요!!)
        post.setMatchedTeam(opponentTeam);

        // 6) 신청자에게 알림: 수락 + 수락한 팀 정보
        Team hostTeam = post.getTeam();
        String hostTeamName = hostTeam != null ? hostTeam.getName() : "상대 팀";
        notificationService.send(
                req.getRequesterUser(),
                NotificationType.MATCH_ACCEPTED.name(),
                hostTeamName + " 팀이 매치 요청을 수락했습니다.",
                req
        );
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

        Team hostTeam = req.getMatchPost().getTeam();
        String hostTeamName = hostTeam != null ? hostTeam.getName() : "상대 팀";
        notificationService.send(
                req.getRequesterUser(),
                NotificationType.MATCH_REJECTED.name(),
                hostTeamName + " 팀이 매치 요청을 거절했습니다.",
                req
        );
    }

    @Transactional(readOnly = true)
    public MatchPost getNearestMatchedMatch() {
        return matchPostRepository
                .findFirstByStatusAndMatchDatetimeAfterOrderByMatchDatetimeAsc(
                        "MATCHED", LocalDateTime.now()
                );
    }

    @Transactional(readOnly = true)
    public List<MatchPost> getUpcomingMatchedMatches() {
        return matchPostRepository
                .findAllByStatusAndMatchDatetimeAfterOrderByMatchDatetimeAsc(
                        "MATCHED", LocalDateTime.now()
                );
    }

    @Transactional(readOnly = true)
    public MatchPost getNearestUpcomingMatch() {

        // 로그인한 유저의 팀 가져오기
        Team myTeam = currentUserService.getCurrentUserTeamOrNull();
        LocalDateTime now = LocalDateTime.now();

        if (myTeam != null) {
            // 내가 등록했거나(or matchedTeam) 내 팀이 참여하는 매치 중 가장 가까운 것
            List<MatchPost> mine = matchPostRepository
                    .findByTeamAndStatusAndMatchDatetimeAfterOrderByMatchDatetimeAsc(
                            myTeam, "MATCHED", now);

            if (!mine.isEmpty()) return mine.get(0);

            List<MatchPost> asOpponent = matchPostRepository
                    .findByMatchedTeamAndStatusAndMatchDatetimeAfterOrderByMatchDatetimeAsc(
                            myTeam, "MATCHED", now);

            if (!asOpponent.isEmpty()) return asOpponent.get(0);
        }

        // 팀이 없거나 내 팀 매치가 없으면 전체 중 가장 가까운 MATCHED 매치 반환
        return matchPostRepository.findFirstByStatusAndMatchDatetimeAfterOrderByMatchDatetimeAsc(
                "MATCHED", now
        );
    }



    @Transactional(readOnly = true)
    public List<MatchPost> getUpcomingMatches() {
        return matchPostRepository
                .findByStatusAndMatchDatetimeAfterOrderByMatchDatetimeAsc(
                        "MATCHED", LocalDateTime.now()
                ).stream()
                .filter(m -> m.getMatchedTeam() != null)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MatchPost> getAvailableMatchPreview(int limit) {

        User user = currentUserService.getCurrentUser();
        Team team = currentUserService.getCurrentUserTeamOrNull();

        // 팀 없으면 아무것도 보여주지 않음
        if (team == null) return List.of();

        // 내가 신청한 matchPostId → status map
        Map<Long, String> myReqMap = getMyRequestStatusMap();

        // 전체 매치
        List<MatchPost> all = matchPostRepository
                .findByStatusAndMatchDatetimeAfterOrderByMatchDatetimeAsc(
                        "OPEN", LocalDateTime.now()
                );

        return all.stream()
                .filter(m -> {

                    // 내가 만든 매치는 제외
                    boolean mine = m.getCreatedBy() != null &&
                            m.getCreatedBy().getId().equals(user.getId());

                    // 이미 신청한 매치는 제외
                    boolean requested = myReqMap.containsKey(m.getId());

                    return !mine && !requested;
                })
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MatchPost> getAvailableMatchesForHome(int limit) {

        User currentUser = currentUserService.getCurrentUser();
        Long myUserId = currentUser.getId();

        // 내가 신청한 매치 상태 map
        Map<Long, String> myRequestMap = getMyRequestStatusMap();

        return matchPostRepository.findByStatusOrderByMatchDatetimeAsc("OPEN")  // 앞으로 있을 오픈 매치
                .stream()
                .filter(post -> {
                    boolean notMine = !post.getCreatedBy().getId().equals(myUserId);
                    boolean notRequested = !myRequestMap.containsKey(post.getId());
                    return notMine && notRequested;
                })
                .limit(limit)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MatchPost> getMyTeamUpcomingMatches() {

        Team myTeam = currentUserService.getCurrentUserTeamOrNull();
        if (myTeam == null) return List.of();

        // 내가 등록한 MATCHED 매치들
        List<MatchPost> created = matchPostRepository
                .findByTeamAndStatusAndMatchDatetimeAfterOrderByMatchDatetimeAsc(
                        myTeam, "MATCHED", LocalDateTime.now()
                );

        // 내가 상대팀으로 매칭된 MATCHED 매치들
        List<MatchPost> accepted = matchPostRepository
                .findByMatchedTeamAndStatusAndMatchDatetimeAfterOrderByMatchDatetimeAsc(
                        myTeam, "MATCHED", LocalDateTime.now()
                );

        // 두 개 합치기
        created.addAll(accepted);
        return created.stream()
                .sorted(Comparator.comparing(MatchPost::getMatchDatetime))
                .collect(Collectors.toList());
    }




}
