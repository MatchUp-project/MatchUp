package com.team10.matchup.media;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "media")
public class Media {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "team_id", nullable = false)
    private Long teamId;

    @Column(name = "uploader_id", nullable = false)
    private Long uploaderId;

    @Column(name = "file_url", length = 255, nullable = false)
    private String fileUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private MediaType type;

    @Column(name = "uploaded_at", insertable = false, updatable = false)
    private LocalDateTime uploadedAt;

    protected Media() {}

    public Media(Long teamId, Long uploaderId, String fileUrl, MediaType type) {
        this.teamId = teamId;
        this.uploaderId = uploaderId;
        this.fileUrl = fileUrl;
        this.type = type;
    }

    public Long getId() { return id; }
    public Long getTeamId() { return teamId; }
    public Long getUploaderId() { return uploaderId; }
    public String getFileUrl() { return fileUrl; }
    public MediaType getType() { return type; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }

    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
    public void setType(MediaType type) { this.type = type; }
}

