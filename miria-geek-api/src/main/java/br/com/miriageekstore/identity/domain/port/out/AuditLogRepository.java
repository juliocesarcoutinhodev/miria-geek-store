package br.com.miriageekstore.identity.domain.port.out;

import br.com.miriageekstore.identity.domain.model.AuditLog;

public interface AuditLogRepository {
    void save(AuditLog auditLog);
}
