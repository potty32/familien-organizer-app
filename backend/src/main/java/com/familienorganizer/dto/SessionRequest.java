package com.familienorganizer.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record SessionRequest(
        @NotNull(message = "Benutzer-ID muss angegeben werden")
        UUID userId,

        String pinCode
) {}
