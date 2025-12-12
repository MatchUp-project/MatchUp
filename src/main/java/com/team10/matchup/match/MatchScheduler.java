package com.team10.matchup.match;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Cleans up expired OPEN matches that never got matched.
 */
@Component
@RequiredArgsConstructor
public class MatchScheduler {

    private final MatchPostRepository matchPostRepository;

    /**
     * Run hourly to remove past OPEN matches with no matched team.
     */
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void cleanExpiredOpenMatches() {
        LocalDateTime now = LocalDateTime.now();
        List<MatchPost> expired = matchPostRepository
                .findAllByStatusAndMatchDatetimeBeforeAndMatchedTeamIsNull("OPEN", now);
        if (expired.isEmpty()) return;
        matchPostRepository.deleteAll(expired);
    }
}
