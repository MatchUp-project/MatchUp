package com.team10.matchup.matchrecord;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MatchRecordStats {
    private final int total;
    private final int wins;
    private final int losses;
    private final int draws;
    private final double winRate; // 0.0 ~ 1.0
}
