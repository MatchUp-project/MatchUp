package com.team10.matchup;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MediaRepository extends JpaRepository<Media, Long> {

    List<Media> findByTeamIdOrderByUploadedAtDesc(Long teamId);
}
