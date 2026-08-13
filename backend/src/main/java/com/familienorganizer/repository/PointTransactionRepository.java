package com.familienorganizer.repository;

import com.familienorganizer.entity.PointTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PointTransactionRepository extends JpaRepository<PointTransaction, UUID> {

    List<PointTransaction> findByUserIdOrderByCreatedAtDesc(UUID userId);
}
