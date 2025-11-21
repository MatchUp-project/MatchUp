package com.team10.matchup;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    // 팀별 일정 조회
    @GetMapping
    public List<EventResponse> getEvents(@RequestParam Long teamId) {
        return eventService.getEventsByTeam(teamId);
    }

    // 일정 생성
    @PostMapping
    public EventResponse createEvent(@RequestBody EventRequest request) {
        return eventService.createEvent(request);
    }

    // 일정 삭제 (옵션)
    @DeleteMapping("/{id}")
    public void deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
    }
}

