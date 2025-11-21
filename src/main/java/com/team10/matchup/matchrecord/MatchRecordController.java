package com.team10.matchup.matchrecord;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/match-records")
public class MatchRecordController {

    private final MatchRecordService service;

    public MatchRecordController(MatchRecordService service) {
        this.service = service;
    }

    @PostMapping
    public MatchRecordResponse create(@RequestBody MatchRecordRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<MatchRecordResponse> list(@RequestParam Long teamId) {
        return service.getByTeam(teamId);
    }

    @GetMapping("/{id}")
    public MatchRecordResponse getOne(@PathVariable Long id) {
        return service.getOne(id);
    }
}

