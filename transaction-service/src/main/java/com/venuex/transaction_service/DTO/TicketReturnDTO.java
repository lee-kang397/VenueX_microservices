package com.venuex.transaction_service.DTO;

import java.math.BigDecimal;

public class TicketReturnDTO {

    private Integer id;
    private String seatSection;
    private BigDecimal price;

    public TicketReturnDTO(Integer id, String seatSection, BigDecimal price) {
        this.id = id;
        this.seatSection = seatSection;
        this.price = price;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSeatSections() {
        return seatSection;
    }

    public void setSeatSection(String seatSection) {
        this.seatSection = seatSection;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}