package com.team10.matchup;

public class MediaRequest {

    private Long teamId;
    private Long uploaderId;
    private String fileUrl;
    private String type;   // "IMAGE" or "VIDEO"

    public Long getTeamId() { return teamId; }
    public Long getUploaderId() { return uploaderId; }
    public String getFileUrl() { return fileUrl; }
    public String getType() { return type; }
}

