package com.familienorganizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateShoppingItemRequest(
        @NotBlank @Size(max = 100) String name,
        String note
) {}
