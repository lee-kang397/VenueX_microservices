package com.venuex.event_service.dto;

public class SeatSectionDTO {

    private Integer id;
    private String type;
    private Integer capacity;

    public SeatSectionDTO() {}
    public SeatSectionDTO(Integer id, String type, Integer capicity) {
        this.id = id;
        this.type = type;
        this.capacity = capicity;
    }
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
}
