CREATE TABLE host_requests (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (
        status IN (
            'PENDING',
            'APPROVED',
            'DENIED'
        )
    ),
    requested_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    reviewed_by INT
);