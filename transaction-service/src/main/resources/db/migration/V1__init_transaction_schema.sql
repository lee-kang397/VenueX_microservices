CREATE TABLE bookings (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    event_id INT NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (
        status IN ('BOOKED', 'CANCELED', 'PENDING', 'FAILED')
    ),
    booked_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- optional snapshots (safe to keep)
    user_email VARCHAR(255),
    event_name VARCHAR(255),
    event_start_time TIMESTAMP
);

CREATE TABLE tickets (
    id SERIAL PRIMARY KEY,
    booking_id INT NOT NULL,
    seat_section_type VARCHAR(50) NOT NULL,
    price NUMERIC(10, 2) NOT NULL CHECK (price >= 0),
    status VARCHAR(20) NOT NULL CHECK (status IN ('HELD', 'ISSUED', 'VOID')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_tickets_booking
        FOREIGN KEY (booking_id) REFERENCES bookings (id) ON DELETE CASCADE
);

CREATE TABLE payment (
    id SERIAL PRIMARY KEY,
    booking_id INT NOT NULL UNIQUE,
    user_id INT NOT NULL,
    amount NUMERIC(10, 2) NOT NULL CHECK (amount >= 0),
    payment_method VARCHAR(50) NOT NULL CHECK (payment_method IN ('CREDIT_CARD')),
    status VARCHAR(20) NOT NULL CHECK (status IN ('INITIATED', 'PAID', 'FAILED')),
    transaction_ref VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_payment_booking
        FOREIGN KEY (booking_id) REFERENCES bookings (id) ON DELETE CASCADE
);

CREATE TABLE notifications (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    booking_id INT NULL,
    type VARCHAR(50) NOT NULL CHECK (type IN ('EMAIL')),
    status VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'SENT', 'FAILED')),
    message TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    sent_at TIMESTAMP NULL,

    CONSTRAINT fk_notifications_booking
        FOREIGN KEY (booking_id) REFERENCES bookings (id) ON DELETE SET NULL
);

CREATE INDEX idx_bookings_user_id ON bookings(user_id);
CREATE INDEX idx_bookings_event_id ON bookings(event_id);
CREATE INDEX idx_tickets_booking_id ON tickets(booking_id);
CREATE INDEX idx_payment_booking_id ON payment(booking_id);
CREATE INDEX idx_notifications_user_id ON notifications(user_id);
CREATE INDEX idx_notifications_status ON notifications(status);