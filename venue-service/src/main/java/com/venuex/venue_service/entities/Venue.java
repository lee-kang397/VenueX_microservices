package com.venuex.venue_service.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "venues")
public class Venue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(name = "location_id")
    private String location;

    @Column(name = "venue_desc", columnDefinition = "TEXT")
    private String venueDescription;

    //constructors (only admins can add venues)
    public Venue() {}
    public Venue(String name, String location, String description){
        this.name = name;
        this.location = location;
        this.venueDescription = description;
    }

    //Getters and Setters 
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return venueDescription;
    }

    //admin priviliages 
    public void setId(Integer id) {
        this.id = id;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDescription(String description) {
        this.venueDescription = description;
    }
}