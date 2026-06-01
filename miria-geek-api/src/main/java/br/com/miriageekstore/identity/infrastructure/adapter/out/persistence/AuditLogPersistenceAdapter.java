package br.com.miriageekstore.identity.infrastructure.adapter.out.persistence;

import br.com.miriageekstore.identity.domain.model.AuditLog;
import br.com.miriageekstore.identity.domain.port.out.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class AuditLogPersistenceAdapter implements AuditLogRepository {

    private final AuditLogJpaRepository jpaRepository;

    @Override
    public void save(AuditLog auditLog) {
        var entity = new AuditLogEntity();
        entity.setId(auditLog.getId());
        entity.setUserId(auditLog.getUserId());
        entity.setAdminId(auditLog.getAdminId());
        entity.setAction(auditLog.getAction());
        entity.setCreatedAt(auditLog.getCreatedAt());
        jpaRepository.save(entity);
    }
}
