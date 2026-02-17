package com.venuex.transaction_service.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bookings")
public class Booking {

    public enum BookingStatus {
        PENDING,
        BOOKED,
        CANCELED,
        FAILED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Reference to user service
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    // Reference to event service
    @Column(name = "event_id", nullable = false)
    private Integer eventId;

    @Column(name = "user_email")
    private String userEmail;

    @Column(name = "event_name")
    private String eventName;

    @Column(name = "event_start_time")
    private LocalDateTime eventStartTime;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Ticket> tickets = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status = BookingStatus.PENDING;

    @Column(name = "booked_at", nullable = false)
    private LocalDateTime bookedAt;

    public Booking() {
    }

    public Booking(Integer userId, Integer eventId) {
        this.userId = userId;
        this.eventId = eventId;
        this.status = BookingStatus.PENDING;
    }

    @PrePersist
    public void prePersist() {
        if (bookedAt == null) {
            bookedAt = LocalDateTime.now();
        }
        if (status == null) {
            status = BookingStatus.PENDING;
        }
    }

    // Getters & Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public LocalDateTime getEventStartTime() {
        return eventStartTime;
    }

    public void setEventStartTime(LocalDateTime eventStartTime) {
        this.eventStartTime = eventStartTime;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets.clear();
        if (tickets != null) {
            tickets.forEach(this::addTicket);
        }
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public LocalDateTime getBookedAt() {
        return bookedAt;
    }

    public void setBookedAt(LocalDateTime bookedAt) {
        this.bookedAt = bookedAt;
    }

    public void addTicket(Ticket ticket) {
        if (ticket == null)
            return;
        tickets.add(ticket);
        ticket.setBooking(this);
    }

    public void removeTicket(Ticket ticket) {
        if (ticket == null)
            return;
        tickets.remove(ticket);
        ticket.setBooking(null);
    }
}