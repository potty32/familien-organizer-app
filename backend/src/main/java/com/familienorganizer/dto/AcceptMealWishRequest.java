package com.familienorganizer.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record AcceptMealWishRequest(
        @NotNull(message = "Datum muss angegeben werden")
        LocalDate weeklyPlanDate
) {}
