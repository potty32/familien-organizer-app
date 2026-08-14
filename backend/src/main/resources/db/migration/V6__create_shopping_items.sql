CREATE TABLE shopping_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    note TEXT,
    status VARCHAR(10) NOT NULL DEFAULT 'PENDING',
    added_by_id UUID NOT NULL REFERENCES family_users(id),
    points_processed BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_shopping_items_status ON shopping_items(status);
