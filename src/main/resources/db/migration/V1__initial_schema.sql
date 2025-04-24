CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       login VARCHAR(255) UNIQUE NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       name VARCHAR(255) NOT NULL,
                       created_at TIMESTAMP NOT NULL
);

CREATE TABLE game_sessions (
                               id BIGSERIAL PRIMARY KEY,
                               status VARCHAR(20) NOT NULL,
                               deck TEXT NOT NULL,
                               current_player_index INT NOT NULL,
                               next_player_skip BOOLEAN NOT NULL,
                               created_by BIGINT REFERENCES users(id),
                               created_at TIMESTAMP NOT NULL,
                               started_at TIMESTAMP,
                               finished_at TIMESTAMP
);