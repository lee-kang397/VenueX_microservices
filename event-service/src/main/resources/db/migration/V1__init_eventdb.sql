CREATE TABLE events (
    id SERIAL PRIMARY KEY,
    venue_id INT NOT NULL,
    created_by INT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'OPEN',
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    start_time TIMESTAMP NOT NULL
);

CREATE TABLE event_seat_sections (
    id SERIAL PRIMARY KEY,
    event_id INT NOT NULL,
    seat_section_id INT NOT NULL,
    price NUMERIC(10,2) NOT NULL,
    remaining_capacity INT NOT NULL,
    UNIQUE (event_id, seat_section_id),
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE
);
