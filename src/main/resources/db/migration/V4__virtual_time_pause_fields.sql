ALTER TABLE gargoyles
    -- virtual time + pause controls
  ADD COLUMN paused BOOLEAN NOT NULL DEFAULT FALSE,
  ADD COLUMN last_tick_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  ADD COLUMN paused_at TIMESTAMPTZ NULL,


  -- “age that only increases while active”
  ADD COLUMN active_minutes BIGINT NOT NULL DEFAULT 0;

