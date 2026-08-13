package com.familienorganizer.dto;

import com.familienorganizer.entity.TaskStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateTaskStatusRequest(
        @NotNull(message = "Status muss angegeben werden")
        TaskStatus status
) {}
