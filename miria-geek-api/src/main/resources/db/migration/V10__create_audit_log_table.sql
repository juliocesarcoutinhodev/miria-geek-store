CREATE TABLE audit_log (
    id          UUID                     NOT NULL PRIMARY KEY,
    user_id     UUID                     NOT NULL,
    admin_id    UUID                     NOT NULL,
    action      VARCHAR(64)              NOT NULL,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_audit_log_user_id  ON audit_log (user_id);
CREATE INDEX idx_audit_log_admin_id ON audit_log (admin_id);
