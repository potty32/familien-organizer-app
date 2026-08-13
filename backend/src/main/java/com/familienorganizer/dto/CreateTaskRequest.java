package com.familienorganizer.dto;

import com.familienorganizer.entity.RecurrencePattern;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.UUID;

public record CreateTaskRequest(
        @NotBlank(message = "Titel darf nicht leer sein")
        @Size(max = 100, message = "Titel darf maximal 100 Zeichen haben")
        String title,

        String description,

        @Min(value = 0, message = "Punkte müssen positiv sein")
        @Max(value = 1000, message = "Maximal 1000 Punkte pro Aufgabe")
        Integer points,

        @NotNull(message = "Zuweisung muss angegeben werden")
        UUID assignedToId,

        LocalDate dueDate,

        boolean recurring,

        RecurrencePattern recurrencePattern
) {}
