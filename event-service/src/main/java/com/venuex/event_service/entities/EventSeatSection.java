package com.venuex.event_service.entities;

import java.math.BigDecimal;

import jakarta.persistence.*;

@Entity
@Table(name = "event_seat_sections")
public class EventSeatSection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "seat_section_id", nullable = false)
    private Integer seatSectionId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "remaining_capacity", nullable = false)
    private Integer remainingCapacity;

    // getters and setters
    public Integer getId() { return id; }
    public Event getEvent() { return event; }
    public Integer getSeatSectionId() { return seatSectionId; }
    public BigDecimal getPrice() { return price; }
    public Integer getRemainingCapacity() { return remainingCapacity; }

    public void setId(Integer id) { this.id = id; }
    public void setEvent(Event event) { this.event = event; }
    public void setSeatSectionId(Integer seatSectionId) { this.seatSectionId = seatSectionId; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setRemainingCapacity(Integer remainingCapacity) { this.remainingCapacity = remainingCapacity; }

}

