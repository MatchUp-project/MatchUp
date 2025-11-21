package com.team10.matchup.media;

import com.team10.matchup.team.TeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MediaService {

    private final MediaRepository mediaRepository;
    private final TeamRepository teamRepository;

    public MediaService(MediaRepository mediaRepository, TeamRepository teamRepository) {
        this.mediaRepository = mediaRepository;
        this.teamRepository = teamRepository;
    }

    public MediaResponse create(MediaRequest req) {
        if (req.getTeamId() == null) {
            throw new IllegalArgumentException("teamId는 필수입니다.");
        }
        if (!teamRepository.existsById(req.getTeamId())) {
            throw new IllegalArgumentException("존재하지 않는 팀입니다. id=" + req.getTeamId());
        }
        if (req.getUploaderId() == null) {
            throw new IllegalArgumentException("uploaderId는 필수입니다.");
        }
        if (req.getFileUrl() == null || req.getFileUrl().isBlank()) {
            throw new IllegalArgumentException("fileUrl은 필수입니다.");
        }
        MediaType type;
        try {
            type = MediaType.valueOf(req.getType() != null ? req.getType().toUpperCase() : "IMAGE");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("type 값은 IMAGE 또는 VIDEO 여야 합니다.");
        }

        Media media = new Media(
                req.getTeamId(),
                req.getUploaderId(),
                req.getFileUrl(),
                type
        );
        return new MediaResponse(mediaRepository.save(media));
    }

    @Transactional(readOnly = true)
    public List<MediaResponse> getByTeam(Long teamId) {
        return mediaRepository.findByTeamIdOrderByUploadedAtDesc(teamId)
                .stream()
                .map(MediaResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public MediaResponse getOne(Long id) {
        Media media = mediaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("미디어를 찾을 수 없습니다. id=" + id));
        return new MediaResponse(media);
    }

    public void delete(Long id) {
        mediaRepository.deleteById(id);
    }
}
