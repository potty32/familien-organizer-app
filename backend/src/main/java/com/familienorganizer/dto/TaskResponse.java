package com.familienorganizer.dto;

import com.familienorganizer.entity.RecurrencePattern;
import com.familienorganizer.entity.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record TaskResponse(
        UUID id,
        String title,
        String description,
        TaskStatus status,
        Integer points,
        TaskUserRef assignedTo,
        TaskUserRef createdBy,
        LocalDate dueDate,
        boolean recurring,
        RecurrencePattern recurrencePattern,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
