package com.team10.matchup.media;

import java.time.LocalDateTime;

public class MediaResponse {

    private Long id;
    private Long teamId;
    private Long uploaderId;
    private String fileUrl;
    private String type;
    private LocalDateTime uploadedAt;

    public MediaResponse(Media media) {
        this.id = media.getId();
        this.teamId = media.getTeamId();
        this.uploaderId = media.getUploaderId();
        this.fileUrl = media.getFileUrl();
        this.type = media.getType().name();
        this.uploadedAt = media.getUploadedAt();
    }

    public Long getId() { return id; }
    public Long getTeamId() { return teamId; }
    public Long getUploaderId() { return uploaderId; }
    public String getFileUrl() { return fileUrl; }
    public String getType() { return type; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }
}

