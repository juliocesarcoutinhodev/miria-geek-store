package br.com.miriageekstore.identity.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface AddressJpaRepository extends JpaRepository<AddressEntity, UUID> {

    List<AddressEntity> findAllByUserId(UUID userId);

    Optional<AddressEntity> findByIdAndUserId(UUID id, UUID userId);

    int countByUserId(UUID userId);

    @Modifying
    @Query("UPDATE AddressEntity a SET a.isDefault = false WHERE a.userId = :userId AND a.isDefault = true")
    void clearDefaultByUserId(@Param("userId") UUID userId);
}
