package com.team10.matchup.chat;

public class ChatMessageRequest {
    private Long roomId;
    private Long senderId;
    private String message;

    public Long getRoomId() { return roomId; }
    public Long getSenderId() { return senderId; }
    public String getMessage() { return message; }
}
