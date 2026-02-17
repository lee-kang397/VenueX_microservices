package com.venuex.transaction_service.DTO;

import java.time.LocalDateTime;

public class EventDTO {

    private Integer id;
    private String venueName;
    private String name;
    private String description;
    private LocalDateTime startTime;
    private String status;

    public EventDTO() {
    }

    public EventDTO(Integer id, String venueName, String name, String description, LocalDateTime startTime,
            String status) {
        this.id = id;
        this.venueName = venueName;
        this.name = name;
        this.description = description;
        this.startTime = startTime;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}