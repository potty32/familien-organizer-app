CREATE TABLE meal_wishes (
    id                UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    name              VARCHAR(100) NOT NULL,
    description       TEXT,
    status            VARCHAR(10)  NOT NULL DEFAULT 'PENDING',
    suggested_by_id   UUID         NOT NULL REFERENCES family_users(id),
    weekly_plan_date  DATE,
    points_awarded    BOOLEAN      NOT NULL DEFAULT false,
    created_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_meal_wishes_status      ON meal_wishes(status);
CREATE INDEX idx_meal_wishes_plan_date   ON meal_wishes(weekly_plan_date);
