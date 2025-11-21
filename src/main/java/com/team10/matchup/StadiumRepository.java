package com.team10.matchup;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StadiumRepository extends JpaRepository<Stadium, Long> {

    List<Stadium> findByRegion(String region);

    List<Stadium> findByIsAvailableTrue();
}

