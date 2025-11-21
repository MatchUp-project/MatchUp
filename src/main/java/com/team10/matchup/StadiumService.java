package com.team10.matchup;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class StadiumService {

    private final StadiumRepository stadiumRepository;

    public StadiumService(StadiumRepository stadiumRepository) {
        this.stadiumRepository = stadiumRepository;
    }

    public StadiumResponse create(StadiumRequest req) {
        if (req.getName() == null || req.getName().isBlank()) {
            throw new IllegalArgumentException("경기장 이름은 필수입니다.");
        }

        Stadium stadium = new Stadium(
                req.getName(),
                req.getAddress(),
                req.getRegion(),
                req.getCapacity(),
                req.getSurface(),
                req.getPhone(),
                req.getIsAvailable() != null ? req.getIsAvailable() : true
        );
        return new StadiumResponse(stadiumRepository.save(stadium));
    }

    @Transactional(readOnly = true)
    public List<StadiumResponse> getAll() {
        return stadiumRepository.findAll()
                .stream()
                .map(StadiumResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<StadiumResponse> getAvailable() {
        return stadiumRepository.findByIsAvailableTrue()
                .stream()
                .map(StadiumResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public StadiumResponse getOne(Long id) {
        Stadium stadium = stadiumRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("경기장을 찾을 수 없습니다. id=" + id));
        return new StadiumResponse(stadium);
    }

    public StadiumResponse update(Long id, StadiumRequest req) {
        Stadium stadium = stadiumRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("경기장을 찾을 수 없습니다. id=" + id));

        if (req.getName() != null && !req.getName().isBlank()) {
            stadium.setName(req.getName());
        }
        stadium.setAddress(req.getAddress());
        stadium.setRegion(req.getRegion());
        stadium.setCapacity(req.getCapacity());
        stadium.setSurface(req.getSurface());
        stadium.setPhone(req.getPhone());
        if (req.getIsAvailable() != null) {
            stadium.setIsAvailable(req.getIsAvailable());
        }

        return new StadiumResponse(stadium);
    }

    public void delete(Long id) {
        stadiumRepository.deleteById(id);
    }
}
