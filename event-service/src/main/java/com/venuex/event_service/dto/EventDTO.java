package com.venuex.event_service.dto;

import java.time.LocalDateTime;

public class EventDTO {

    private Integer id;
    private Integer venueId;
    private String name;
    private String description;
    private LocalDateTime startTime;
    private String status;

    public EventDTO() {
    }

    public EventDTO(Integer id,Integer venueId,String name,String description,LocalDateTime startTime,String status) {
        this.id = id;
        this.venueId = venueId;
        this.name = name;
        this.description = description;
        this.startTime = startTime;
        this.status = status;
    }

    // Getters
    public Integer getId() { return id; }
    public Integer getVenueId() { return venueId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public LocalDateTime getStartTime() { return startTime; }
    public String getStatus() { return status; }

    // Setters
    public void setId(Integer id) { this.id = id; }
    public void setVenueId(Integer venueId) { this.venueId = venueId; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public void setStatus(String status) { this.status = status; }
}
