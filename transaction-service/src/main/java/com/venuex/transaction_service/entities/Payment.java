package com.venuex.transaction_service.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
public class Payment {

    public enum PaymentStatus {
        INITIATED,
        PAID,
        FAILED
    }

    public enum PaymentMethod {
        CREDIT_CARD
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_id", nullable = false, unique = true)
    private Booking booking;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 50)
    private PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status = PaymentStatus.INITIATED;

    @Column(name = "transaction_ref", length = 100)
    private String transactionRef;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public Payment() {
    }

    public Payment(Booking booking, Integer userId, BigDecimal amount) {
        this.booking = booking;
        this.userId = userId;
        this.amount = amount;
        this.status = PaymentStatus.INITIATED;
        this.paymentMethod = PaymentMethod.CREDIT_CARD;
    }

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = PaymentStatus.INITIATED;
        }
        if (paymentMethod == null) {
            paymentMethod = PaymentMethod.CREDIT_CARD;
        }
    }

    // Getters & setters
    public Integer getId() {
        return id;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public String getTransactionRef() {
        return transactionRef;
    }

    public void setTransactionRef(String transactionRef) {
        this.transactionRef = transactionRef;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}