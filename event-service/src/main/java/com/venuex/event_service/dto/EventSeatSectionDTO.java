package com.venuex.event_service.dto;

import java.math.BigDecimal;

public class EventSeatSectionDTO {
    private String seatSectionName;
    private BigDecimal price;
    private Integer capacity;

    public EventSeatSectionDTO() {}

    public EventSeatSectionDTO(String seatSectionName, BigDecimal price, Integer capacity) {
        this.seatSectionName = seatSectionName;
        this.price = price;
        this.capacity = capacity;
    }

    public String getSeatSectionName() { return seatSectionName; }
    public void setSeatSectionName(String seatSectionName) { this.seatSectionName = seatSectionName; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getCapacity() {return capacity; }
    public void setCapicity(Integer capacity) { this.capacity = capacity; }
}
