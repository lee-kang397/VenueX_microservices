package com.venuex.transaction_service.DTO;

import java.math.BigDecimal;

public class PaymentDTO {

    private Integer paymentId;
    private Integer bookingId;
    private BigDecimal amount;
    private String status;

    public PaymentDTO() {
    }

    public PaymentDTO(Integer paymentId, Integer bookingId, BigDecimal amount, String status) {
        this.paymentId = paymentId;
        this.bookingId = bookingId;
        this.amount = amount;
        this.status = status;
    }

    public Integer getPaymentId() {
        return paymentId;
    }

    public Integer getBookingId() {
        return bookingId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}