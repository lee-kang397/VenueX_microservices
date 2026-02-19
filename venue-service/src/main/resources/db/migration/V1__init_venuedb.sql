CREATE TABLE venues (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    location_id VARCHAR(255),
    venue_desc TEXT
);

CREATE TABLE seat_sections (
    id SERIAL PRIMARY KEY,
    venue_id INT NOT NULL,
    type VARCHAR(50) NOT NULL,
    capacity INT NOT NULL CHECK (capacity > 0),
    FOREIGN KEY (venue_id) REFERENCES venues(id) ON DELETE CASCADE
);
