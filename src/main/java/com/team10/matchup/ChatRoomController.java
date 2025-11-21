package com.team10.matchup;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat/rooms")
public class ChatRoomController {

    private final ChatRoomService service;

    public ChatRoomController(ChatRoomService service) {
        this.service = service;
    }

    @PostMapping
    public ChatRoom create(@RequestBody ChatRoomRequest req) {
        return service.create(req);
    }

    @GetMapping
    public List<ChatRoom> list(@RequestParam Long teamId) {
        return service.getRooms(teamId);
    }
}

