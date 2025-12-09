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
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MatchService {

    private final MatchPostRepository matchPostRepository;
    private final MatchRequestRepository matchRequestRepository;
    private final CurrentUserService currentUserService;

    /* ===================== 조회 ===================== */

    // 전체 매치 가져오기
    @Transactional(readOnly = true)
    public List<MatchPost> getAllMatchPosts() {
        return matchPostRepository.findAllByOrderByCreatedAtDesc();
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
        req.setStatus("PENDING");

        matchRequestRepository.save(req);
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

        List<MatchPost> list = matchPostRepository
                .findByStatusAndMatchDatetimeAfterOrderByMatchDatetimeAsc(
                        "MATCHED", LocalDateTime.now()
                );

        // matchedTeam 없는 잘못된 데이터 제거
        list = list.stream()
                .filter(m -> m.getMatchedTeam() != null)
                .toList();

        return list.isEmpty() ? null : list.get(0);
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




}
