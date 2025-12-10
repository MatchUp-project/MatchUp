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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    @Transactional(readOnly = true)
    public Team getCurrentTeamOrNull() {
        return currentUserService.getCurrentUserTeamOrNull();
    }

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

    // matchId -> 상대 팀 이름 맵
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

    public void saveRecord(MatchRecordForm form) {
        Team myTeam = getCurrentTeamOrNull();
        if (myTeam == null) {
            throw new IllegalStateException("로그인한 사용자의 팀을 찾을 수 없습니다.");
        }

        if (form.getMatchId() == null) {
            throw new IllegalArgumentException("매치 정보가 없습니다.");
        }

        if (form.getTeam2Id() == null) {
            throw new IllegalArgumentException("상대 팀을 선택해주세요.");
        }

        Team opponent = teamRepository.findById(form.getTeam2Id())
                .orElseThrow(() -> new IllegalArgumentException("상대 팀을 찾을 수 없습니다."));

        MatchPost post = matchPostRepository.findById(form.getMatchId())
                .orElseThrow(() -> new IllegalArgumentException("매치 정보를 찾을 수 없습니다."));

        boolean relatedToMyTeam =
                (post.getTeam() != null && post.getTeam().getId().equals(myTeam.getId())) ||
                        (post.getMatchedTeam() != null && post.getMatchedTeam().getId().equals(myTeam.getId()));
        if (!relatedToMyTeam) {
            throw new IllegalStateException("내 팀과 관련 없는 매치입니다.");
        }

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

        // 점수 입력 완료 후 상태 변경 → "점수 입력 필요" 목록에서 제거
        post.setStatus("RECORDED");
    }

    @Transactional(readOnly = true)
    public MatchRecordForm createFormFromAcceptedMatch(Long matchId) {
        MatchPost post = matchPostRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("매칭된 경기를 찾을 수 없습니다."));

        Team myTeam = getCurrentTeamOrNull();
        if (myTeam == null) {
            return new MatchRecordForm();
        }

        MatchRecordForm form = new MatchRecordForm();
        form.setMatchId(matchId);

        // 1) 날짜/시간
        LocalDateTime dt = post.getMatchDatetime();
        if (dt != null) {
            form.setMatchDate(dt.toLocalDate());
            form.setMatchTime(dt.toLocalTime());
        }
        form.setPlace(post.getLocation());

        // 2) 상대 팀
        Team opponentTeam = findOpponentTeamForMatch(matchId, myTeam);
        if (opponentTeam != null) {
            form.setTeam2Id(opponentTeam.getId());
            form.setTeam2Name(opponentTeam.getName());
        }

        // 3) 초기 점수 0
        form.setTeam1Score(0);
        form.setTeam2Score(0);

        return form;
    }

    /**
     * matchId + 내 팀 기준으로 상대 팀 찾기
     */
    @Transactional(readOnly = true)
    protected Team findOpponentTeamForMatch(Long matchId, Team myTeam) {

        List<MatchRequest> acceptedRequests =
                matchRequestRepository.findByMatchPost_IdAndStatus(matchId, "ACCEPTED");

        for (MatchRequest req : acceptedRequests) {
            Long requesterUserId = req.getRequesterUser().getId();

            Optional<TeamMember> tmOpt = teamMemberRepository.findFirstByUser_Id(requesterUserId);

            if (tmOpt.isPresent()) {
                Team candidateTeam = tmOpt.get().getTeam();

                if (!candidateTeam.getId().equals(myTeam.getId())) {
                    return candidateTeam;
                }
            }
        }

        return null;
    }
}
