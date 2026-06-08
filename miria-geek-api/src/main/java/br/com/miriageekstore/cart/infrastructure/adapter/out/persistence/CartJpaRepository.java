package br.com.miriageekstore.cart.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

interface CartJpaRepository extends JpaRepository<CartEntity, UUID> {
    Optional<CartEntity> findByUserId(UUID userId);

    @Query("SELECT c FROM CartEntity c JOIN c.items i WHERE i.id = :itemId")
    Optional<CartEntity> findByItemId(@Param("itemId") UUID itemId);
}
