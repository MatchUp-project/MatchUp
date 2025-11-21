package com.team10.matchup.stadium;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stadiums")
public class StadiumController {

    private final StadiumService stadiumService;

    public StadiumController(StadiumService stadiumService) {
        this.stadiumService = stadiumService;
    }

    // 전체 목록
    @GetMapping
    public List<StadiumResponse> getAll() {
        return stadiumService.getAll();
    }

    // 사용 가능 경기장만
    @GetMapping("/available")
    public List<StadiumResponse> getAvailable() {
        return stadiumService.getAvailable();
    }

    // 단건 조회
    @GetMapping("/{id}")
    public StadiumResponse getOne(@PathVariable Long id) {
        return stadiumService.getOne(id);
    }

    // 생성
    @PostMapping
    public StadiumResponse create(@RequestBody StadiumRequest req) {
        return stadiumService.create(req);
    }

    // 수정
    @PutMapping("/{id}")
    public StadiumResponse update(@PathVariable Long id, @RequestBody StadiumRequest req) {
        return stadiumService.update(id, req);
    }

    // 삭제
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        stadiumService.delete(id);
    }
}

