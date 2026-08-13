package com.familienorganizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateMealWishRequest(
        @NotBlank(message = "Name darf nicht leer sein")
        @Size(max = 100, message = "Name darf maximal 100 Zeichen haben")
        String name,

        String description
) {}
