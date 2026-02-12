package main.java.com.venuex.event_service.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "seat_sections")
public class SeatSection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private Integer capacity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    public SeatSection() {}

    public SeatSection(Integer id, String type, Integer capacity, Venue venue) {
        this.id = id;
        this.type = type;
        this.capacity = capacity;
        this.venue = venue;
    }

    //getters and setters 
    public Integer getId() {
        return this.id;
    }
    
    public String getType() {
        return this.type;
    }

    public Integer getCapacity() {
        return this.capacity;
    }

    public Venue getVenue() {
        return this.venue;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }
}