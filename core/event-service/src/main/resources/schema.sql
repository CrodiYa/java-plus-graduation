CREATE SCHEMA IF NOT EXISTS event_schema;

CREATE TABLE IF NOT EXISTS event_schema.category (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    CONSTRAINT UQ_CATEGORY_NAME UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS event_schema.event (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title VARCHAR(120) NOT NULL,
    annotation VARCHAR(2000) NOT NULL,
    description VARCHAR(7000) NOT NULL,
    lat DECIMAL(10,6) NOT NULL,
    lon DECIMAL(10,6) NOT NULL,
    event_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    category_id BIGINT NOT NULL,
    initiator_id BIGINT NOT NULL,
    paid BOOLEAN DEFAULT FALSE,
    state VARCHAR(50) NOT NULL,
    participant_limit INT,
    request_moderation BOOLEAN DEFAULT FALSE,
    published_on TIMESTAMP WITHOUT TIME ZONE,
    created_on TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES event_schema.category(id)
);

CREATE TABLE IF NOT EXISTS event_schema.compilation (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    pinned BOOLEAN NOT NULL,
    title VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS event_schema.compilation_event (
    compilation_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    PRIMARY KEY (compilation_id, event_id),
    FOREIGN KEY (compilation_id) REFERENCES event_schema.compilation(id),
    FOREIGN KEY (event_id) REFERENCES event_schema.event(id)
);