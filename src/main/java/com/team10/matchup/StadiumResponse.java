package com.team10.matchup;

import java.time.LocalDateTime;

public class StadiumResponse {

    private Long id;
    private String name;
    private String address;
    private String region;
    private Integer capacity;
    private String surface;
    private String phone;
    private Boolean isAvailable;
    private LocalDateTime createdAt;

    public StadiumResponse(Stadium stadium) {
        this.id = stadium.getId();
        this.name = stadium.getName();
        this.address = stadium.getAddress();
        this.region = stadium.getRegion();
        this.capacity = stadium.getCapacity();
        this.surface = stadium.getSurface();
        this.phone = stadium.getPhone();
        this.isAvailable = stadium.getIsAvailable();
        this.createdAt = stadium.getCreatedAt();
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getRegion() { return region; }
    public Integer getCapacity() { return capacity; }
    public String getSurface() { return surface; }
    public String getPhone() { return phone; }
    public Boolean getIsAvailable() { return isAvailable; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}

