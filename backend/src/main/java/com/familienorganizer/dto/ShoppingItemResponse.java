package com.familienorganizer.dto;

import com.familienorganizer.entity.ShoppingItemStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record ShoppingItemResponse(
        UUID id,
        String name,
        String note,
        ShoppingItemStatus status,
        TaskUserRef addedBy,
        boolean pointsProcessed,
        LocalDateTime createdAt
) {}
