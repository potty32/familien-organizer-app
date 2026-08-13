CREATE TABLE point_transactions (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID        NOT NULL REFERENCES family_users(id),
    task_id     UUID        REFERENCES tasks(id) ON DELETE SET NULL,
    points      INTEGER     NOT NULL,
    reason      TEXT        NOT NULL,
    created_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_point_transactions_user ON point_transactions(user_id);
CREATE INDEX idx_point_transactions_created ON point_transactions(created_at DESC);
