CREATE TABLE family_users (
    id            UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    display_name  VARCHAR(50)  NOT NULL,
    avatar_color  VARCHAR(7)   NOT NULL,
    role          VARCHAR(10)  NOT NULL,
    total_points  INTEGER      NOT NULL DEFAULT 0,
    pin_code      VARCHAR(4),
    active        BOOLEAN      NOT NULL DEFAULT true,
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Seed: 5 Familienmitglieder
INSERT INTO family_users (display_name, avatar_color, role) VALUES
    ('Mama',   '#EC4899', 'PARENT'),
    ('Papa',   '#3B82F6', 'PARENT'),
    ('Kind 1', '#10B981', 'CHILD'),
    ('Kind 2', '#F59E0B', 'CHILD'),
    ('Kind 3', '#8B5CF6', 'CHILD');
