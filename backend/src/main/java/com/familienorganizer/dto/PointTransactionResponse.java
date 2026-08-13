package com.familienorganizer.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record PointTransactionResponse(
        UUID id,
        int points,
        String reason,
        UUID taskId,
        LocalDateTime createdAt
) {}
