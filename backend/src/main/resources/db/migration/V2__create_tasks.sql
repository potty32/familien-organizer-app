CREATE TABLE tasks (
    id                  UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    title               VARCHAR(100) NOT NULL,
    description         TEXT,
    status              VARCHAR(15)  NOT NULL DEFAULT 'OPEN',
    points              INTEGER,
    assigned_to_id      UUID         NOT NULL REFERENCES family_users(id),
    created_by_id       UUID         NOT NULL REFERENCES family_users(id),
    due_date            DATE,
    recurring           BOOLEAN      NOT NULL DEFAULT false,
    recurrence_pattern  VARCHAR(10),
    created_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_tasks_assigned_to ON tasks(assigned_to_id);
CREATE INDEX idx_tasks_status       ON tasks(status);
