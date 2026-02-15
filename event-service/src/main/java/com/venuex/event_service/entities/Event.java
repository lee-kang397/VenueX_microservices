package com.venuex.event_service.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "venue_id", nullable = false)
    private Integer venueId;

    @Column(name = "created_by", nullable = false)
    private Integer createdByUserId;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private String status = "OPEN";

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventSeatSection> seatSections = new ArrayList<>();

    //getters and setters
    public Integer getId() {return id;}

    public Integer getVenueId() { return venueId; }

    public Integer getCreatedByUserId() { return createdByUserId; }

    public String getName() {return name;}

    public String getDescription() {return description;}

    public LocalDateTime getStartTime() {return startTime;}

    public List<EventSeatSection> getSeatSections() {return seatSections;}

    public String getStatus() {return status;}

    public void setId(Integer id) {this.id = id;}

    public void setVenueId(Integer venueId) { this.venueId = venueId; }

    public void setCreatedByUserId(Integer createdByUserId) { this.createdByUserId = createdByUserId; }

    public void setName(String name) {this.name = name;}

    public void setDescription(String description) {this.description = description;}

    public void setStartTime(LocalDateTime startTime) {this.startTime = startTime;}

    public void setSeatSections(List<EventSeatSection> seatSections) {this.seatSections = seatSections;}

    public void setStatus (String status) {this.status = status;}
}