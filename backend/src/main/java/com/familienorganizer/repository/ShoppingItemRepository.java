package com.familienorganizer.repository;

import com.familienorganizer.entity.ShoppingItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ShoppingItemRepository extends JpaRepository<ShoppingItem, UUID> {
    List<ShoppingItem> findAllByOrderByCreatedAtDesc();
}
