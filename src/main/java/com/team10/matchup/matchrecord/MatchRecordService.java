package com.team10.matchup.matchrecord;

import com.team10.matchup.common.CurrentUserService;
import com.team10.matchup.match.MatchPost;
import com.team10.matchup.match.MatchPostRepository;
import com.team10.matchup.match.MatchRequest;
import com.team10.matchup.match.MatchRequestRepository;
import com.team10.matchup.team.Team;
import com.team10.matchup.team.TeamMember;
import com.team10.matchup.team.TeamMemberRepository;
import com.team10.matchup.team.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

// 🔽 여기 두 개 추가
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class MatchRecordService {

    private final MatchRecordRepository matchRecordRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final CurrentUserService currentUserService;
    private final MatchPostRepository matchPostRepository;
    private final MatchRequestRepository matchRequestRepository;

    // ─────────────────── 공통 유틸 ───────────────────

    @Transactional(readOnly = true)
    public Team getCurrentTeamOrNull() {
        return currentUserService.getCurrentUserTeamOrNull();
    }

    // ─────────────────── 목록용 메서드 ───────────────────

    @Transactional(readOnly = true)
    public List<MatchRecord> getRecordsForCurrentTeam() {
        Team myTeam = getCurrentTeamOrNull();
        if (myTeam == null) return List.of();
        return matchRecordRepository.findByTeam1OrTeam2OrderByMatchDateDesc(myTeam, myTeam);
    }

    @Transactional(readOnly = true)
    public List<MatchPost> getMatchedPostsForCurrentTeam() {
        Team myTeam = getCurrentTeamOrNull();
        if (myTeam == null) return List.of();
        return matchPostRepository.findByTeamAndStatusOrderByMatchDatetimeDesc(myTeam, "MATCHED");
    }

    // ✅ 점수 입력 필요 매치들에 대해 matchId -> 상대 팀 이름 맵 생성
    @Transactional(readOnly = true)
    public Map<Long, String> getOpponentNamesForMatches(List<MatchPost> posts) {
        Team myTeam = getCurrentTeamOrNull();
        if (myTeam == null) {
            return Map.of();
        }

        Map<Long, String> result = new HashMap<>();

        for (MatchPost post : posts) {
            Team opponent = findOpponentTeamForMatch(post.getId(), myTeam);
            String name = (opponent != null) ? opponent.getName() : "상대 팀";
            result.put(post.getId(), name);
        }

        return result;
    }

    // ─────────────────── 점수 저장 ───────────────────

    public void saveRecord(MatchRecordForm form) {
        Team myTeam = getCurrentTeamOrNull();
        if (myTeam == null) {
            throw new IllegalStateException("사용자가 속한 팀이 없습니다.");
        }

        if (form.getTeam2Id() == null) {
            throw new IllegalArgumentException("상대 팀이 선택되지 않았습니다.");
        }

        Team opponent = teamRepository.findById(form.getTeam2Id())
                .orElseThrow(() -> new IllegalArgumentException("상대 팀을 찾을 수 없습니다."));

        MatchRecord record = new MatchRecord();
        record.setTeam1(myTeam);
        record.setTeam2(opponent);

        record.setTeam1Score(form.getTeam1Score());
        record.setTeam2Score(form.getTeam2Score());
        record.setPlace(form.getPlace());
        record.setSummary(form.getSummary());
        record.setThumbnailUrl(form.getThumbnailUrl());

        if (form.getMatchDate() != null) {
            LocalTime t = (form.getMatchTime() != null) ? form.getMatchTime() : LocalTime.of(0, 0);
            record.setMatchDate(LocalDateTime.of(form.getMatchDate(), t));
        }

        matchRecordRepository.save(record);
    }

    // ─────────────────── 점수 입력 폼 생성 ───────────────────

    @Transactional(readOnly = true)
    public MatchRecordForm createFormFromAcceptedMatch(Long matchId) {
        MatchPost post = matchPostRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("매치를 찾을 수 없습니다."));

        Team myTeam = getCurrentTeamOrNull();
        if (myTeam == null) {
            return new MatchRecordForm();
        }

        MatchRecordForm form = new MatchRecordForm();

        // 1) 경기 일시 / 장소
        LocalDateTime dt = post.getMatchDatetime();
        if (dt != null) {
            form.setMatchDate(dt.toLocalDate());
            form.setMatchTime(dt.toLocalTime());
        }
        form.setPlace(post.getLocation());

        // 2) 상대 팀 찾기
        Team opponentTeam = findOpponentTeamForMatch(matchId, myTeam);
        if (opponentTeam != null) {
            form.setTeam2Id(opponentTeam.getId());
            form.setTeam2Name(opponentTeam.getName());
        }

        // 3) 기본 점수 0
        form.setTeam1Score(0);
        form.setTeam2Score(0);

        return form;
    }

    /**
     * matchId + 내 팀을 기준으로, 수락된 매치 요청 중 "상대 팀" 을 찾아주는 메서드
     */
    @Transactional(readOnly = true)
    protected Team findOpponentTeamForMatch(Long matchId, Team myTeam) {

        // ✅ status 를 "ACCEPTED" 문자열로 조회한다 (Enum 아님)
        List<MatchRequest> acceptedRequests =
                matchRequestRepository.findByMatchPost_IdAndStatus(matchId, "ACCEPTED");

        for (MatchRequest req : acceptedRequests) {
            // 요청을 보낸 유저 (필드 이름에 맞게 getRequesterUser / getRequester 중 하나일 것)
            Long requesterUserId = req.getRequesterUser().getId();

            // 그 유저가 어떤 팀에 속해 있는지 찾는다.
            Optional<TeamMember> tmOpt = teamMemberRepository.findFirstByUser_Id(requesterUserId);

            if (tmOpt.isPresent()) {
                Team candidateTeam = tmOpt.get().getTeam();

                // 내 팀과 다르면 그 팀이 상대 팀
                if (!candidateTeam.getId().equals(myTeam.getId())) {
                    return candidateTeam;
                }
            }
        }

        // 찾지 못한 경우
        return null;
    }
}