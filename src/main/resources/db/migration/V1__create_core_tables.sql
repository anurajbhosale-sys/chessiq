CREATE EXTENSION IF NOT EXISTS "pgcrypto";
CREATE TABLE players (
                         id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                         chess_com_username      VARCHAR(100) NOT NULL UNIQUE,
                         chess_com_id            BIGINT UNIQUE,
                         display_name            VARCHAR(255),
                         country_code            CHAR(2),
                         joined_chess_com_at     TIMESTAMPTZ,
                         last_synced_at          TIMESTAMPTZ,
                         sync_status             VARCHAR(20) NOT NULL DEFAULT 'NEVER_SYNCED',
                         rapid_rating_current    INTEGER,
                         rapid_rating_best       INTEGER,
                         rapid_wins              INTEGER NOT NULL DEFAULT 0,
                         rapid_losses            INTEGER NOT NULL DEFAULT 0,
                         rapid_draws             INTEGER NOT NULL DEFAULT 0,
                         tactics_rating_best     INTEGER,
                         created_at              TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                         updated_at              TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE TABLE games (
                       id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       player_id           UUID NOT NULL REFERENCES players(id) ON DELETE CASCADE,
                       chess_com_game_id   VARCHAR(100) NOT NULL,
                       played_at           TIMESTAMPTZ NOT NULL,
                       time_class          VARCHAR(20) NOT NULL,
                       time_control        VARCHAR(20) NOT NULL,
                       player_color        VARCHAR(5) NOT NULL,
                       player_rating       INTEGER NOT NULL,
                       opponent_username   VARCHAR(100) NOT NULL,
                       opponent_rating     INTEGER NOT NULL,
                       result              VARCHAR(20) NOT NULL,
                       termination_reason  VARCHAR(30) NOT NULL,
                       player_accuracy     NUMERIC(5,2),
                       opponent_accuracy   NUMERIC(5,2),
                       eco_code            VARCHAR(10),
                       opening_name        VARCHAR(255),
                       opening_url         VARCHAR(500),
                       total_moves         INTEGER,
                       pgn                 TEXT NOT NULL,
                       raw_data            JSONB,
                       analysis_status     VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                       UNIQUE(player_id, chess_com_game_id)
);
CREATE TABLE opening_stats (
                               id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                               player_id       UUID NOT NULL REFERENCES players(id) ON DELETE CASCADE,
                               eco_code        VARCHAR(10) NOT NULL,
                               opening_name    VARCHAR(255) NOT NULL,
                               color           VARCHAR(5) NOT NULL,
                               games_played    INTEGER NOT NULL DEFAULT 0,
                               wins            INTEGER NOT NULL DEFAULT 0,
                               losses          INTEGER NOT NULL DEFAULT 0,
                               draws           INTEGER NOT NULL DEFAULT 0,
                               avg_accuracy    NUMERIC(5,2),
                               last_played_at  TIMESTAMPTZ,
                               updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                               UNIQUE(player_id, eco_code, color)
);
CREATE TABLE sync_jobs (
                           id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                           player_id       UUID NOT NULL REFERENCES players(id) ON DELETE CASCADE,
                           status          VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                           archive_url     VARCHAR(500) NOT NULL,
                           games_found     INTEGER,
                           games_imported  INTEGER,
                           error_message   TEXT,
                           started_at      TIMESTAMPTZ,
                           completed_at    TIMESTAMPTZ,
                           created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_games_player_played    ON games(player_id, played_at DESC);
CREATE INDEX idx_games_player_color     ON games(player_id, player_color);
CREATE INDEX idx_games_eco              ON games(player_id, eco_code);
CREATE INDEX idx_games_result           ON games(player_id, result);
CREATE INDEX idx_games_analysis_status  ON games(analysis_status);
CREATE INDEX idx_opening_stats_player   ON opening_stats(player_id);
CREATE INDEX idx_sync_jobs_player       ON sync_jobs(player_id, created_at DESC);
CREATE INDEX idx_sync_jobs_status       ON sync_jobs(status);