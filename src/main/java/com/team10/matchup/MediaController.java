package com.team10.matchup;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/media")
public class MediaController {

    private final MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    // 팀별 미디어 목록
    @GetMapping
    public List<MediaResponse> list(@RequestParam Long teamId) {
        return mediaService.getByTeam(teamId);
    }

    // 단건 조회 (필요하면 사용)
    @GetMapping("/{id}")
    public MediaResponse getOne(@PathVariable Long id) {
        return mediaService.getOne(id);
    }

    // 등록
    @PostMapping
    public MediaResponse create(@RequestBody MediaRequest request) {
        return mediaService.create(request);
    }

    // 삭제
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        mediaService.delete(id);
    }
}

