package com.team10.matchup;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
@Transactional
public class MatchRecordService {

    private final MatchRecordRepository repo;
    private final TeamRepository teamRepository;

    public MatchRecordService(MatchRecordRepository repo, TeamRepository teamRepository) {
        this.repo = repo;
        this.teamRepository = teamRepository;
    }

    public MatchRecordResponse create(MatchRecordRequest req) {

        if (!teamRepository.existsById(req.getTeam1Id()))
            throw new IllegalArgumentException("team1Id 팀이 존재하지 않습니다.");

        if (!teamRepository.existsById(req.getTeam2Id()))
            throw new IllegalArgumentException("team2Id 팀이 존재하지 않습니다.");

        LocalDateTime matchDate = parseOrNull(req.getMatchDate());

        MatchRecord record = new MatchRecord(
                req.getTeam1Id(),
                req.getTeam2Id(),
                req.getTeam1Score(),
                req.getTeam2Score(),
                matchDate,
                req.getPlace(),
                req.getSummary(),
                req.getThumbnailUrl()
        );

        return new MatchRecordResponse(repo.save(record));
    }

    @Transactional(readOnly = true)
    public List<MatchRecordResponse> getByTeam(Long teamId) {
        return repo.findByTeam1IdOrTeam2IdOrderByMatchDateDesc(teamId, teamId)
                .stream()
                .map(MatchRecordResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public MatchRecordResponse getOne(Long id) {
        return new MatchRecordResponse(
                repo.findById(id).orElseThrow(() ->
                        new IllegalArgumentException("기록이 존재하지 않습니다."))
        );
    }

    private LocalDateTime parseOrNull(String txt) {
        if (txt == null || txt.isBlank()) return null;
        try {
            return LocalDateTime.parse(txt);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("날짜 형식이 올바르지 않습니다: " + txt);
        }
    }
}

