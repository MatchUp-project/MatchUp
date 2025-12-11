package com.team10.matchup.match;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class MatchCreateForm {

    private int playerCount;
    private String location;
    private LocalDate date;
    private LocalTime time;
}