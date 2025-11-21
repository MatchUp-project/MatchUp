package com.team10.matchup.stadium;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "stadium")
public class Stadium {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 200)
    private String address;

    @Column(length = 50)
    private String region;

    private Integer capacity;

    @Column(length = 50)
    private String surface;

    @Column(length = 30)
    private String phone;

    @Column(name = "is_available")
    private Boolean isAvailable = true;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    protected Stadium() {}

    public Stadium(String name, String address, String region,
                   Integer capacity, String surface, String phone, Boolean isAvailable) {
        this.name = name;
        this.address = address;
        this.region = region;
        this.capacity = capacity;
        this.surface = surface;
        this.phone = phone;
        this.isAvailable = isAvailable;
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

    public void setName(String name) { this.name = name; }
    public void setAddress(String address) { this.address = address; }
    public void setRegion(String region) { this.region = region; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    public void setSurface(String surface) { this.surface = surface; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setIsAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; }
}

