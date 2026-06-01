package br.com.miriageekstore.identity.domain.model;

import java.time.Instant;
import java.util.UUID;

public class AuditLog {

    private final UUID id;
    private final UUID userId;
    private final UUID adminId;
    private final AuditAction action;
    private final Instant createdAt;

    private AuditLog(UUID id, UUID userId, UUID adminId, AuditAction action, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.adminId = adminId;
        this.action = action;
        this.createdAt = createdAt;
    }

    public static AuditLog create(UUID userId, UUID adminId, AuditAction action) {
        return new AuditLog(UUID.randomUUID(), userId, adminId, action, Instant.now());
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public UUID getAdminId() { return adminId; }
    public AuditAction getAction() { return action; }
    public Instant getCreatedAt() { return createdAt; }
}
