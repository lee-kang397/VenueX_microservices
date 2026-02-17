package com.venuex.transaction_service.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
public class Ticket {

    public enum TicketStatus {
        HELD,
        ISSUED,
        VOID
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Local relationship inside this microservice DB (allowed).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    /**
     * Snapshot of seat section from event-service.
     * No FK / no entity relationship.
     */
    @Column(name = "seat_section_type", nullable = false, length = 50)
    private String seatSectionType;

    /**
     * Price snapshot at time of booking.
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TicketStatus status = TicketStatus.HELD;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Constructors
    public Ticket() {
    }

    public Ticket(String seatSectionType, BigDecimal price) {
        this.seatSectionType = seatSectionType;
        this.price = price;
        this.status = TicketStatus.HELD;
    }

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = TicketStatus.HELD;
        }
    }

    // Getters & Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public String getSeatSectionType() {
        return seatSectionType;
    }

    public void setSeatSectionType(String seatSectionType) {
        this.seatSectionType = seatSectionType;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}