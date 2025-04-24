CREATE TABLE participations (
                                id BIGSERIAL PRIMARY KEY,
                                game_session_id BIGINT NOT NULL REFERENCES game_sessions(id),
                                user_id BIGINT NOT NULL REFERENCES users(id),
                                score INT NOT NULL,
                                is_active BOOLEAN NOT NULL,
                                CONSTRAINT unique_participation UNIQUE (game_session_id, user_id)
);

CREATE INDEX idx_participations_game_session ON participations (game_session_id);
CREATE INDEX idx_participations_user ON participations (user_id);

CREATE TABLE turns (
                       id BIGSERIAL PRIMARY KEY,
                       game_session_id BIGINT NOT NULL REFERENCES game_sessions(id),
                       user_id BIGINT NOT NULL REFERENCES users(id),
                       card TEXT NOT NULL,
                       result_score INT NOT NULL,
                       created_at TIMESTAMP NOT NULL,
                       description TEXT NOT NULL
);

CREATE INDEX idx_turns_game_session ON turns (game_session_id);
CREATE INDEX idx_turns_user ON turns (user_id);