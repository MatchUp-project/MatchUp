package com.team10.matchup.chat;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat/messages")
public class ChatMessageController {

    private final ChatMessageService service;

    public ChatMessageController(ChatMessageService service) {
        this.service = service;
    }

    @PostMapping
    public ChatMessage create(@RequestBody ChatMessageRequest req) {
        return service.create(req);
    }

    @GetMapping
    public List<ChatMessage> list(@RequestParam Long roomId) {
        return service.getMessages(roomId);
    }
}
