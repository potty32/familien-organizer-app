package com.familienorganizer.dto;

import com.familienorganizer.entity.MealWishStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record MealWishResponse(
        UUID id,
        String name,
        String description,
        MealWishStatus status,
        TaskUserRef suggestedBy,
        LocalDate weeklyPlanDate,
        boolean pointsAwarded,
        LocalDateTime createdAt
) {}
