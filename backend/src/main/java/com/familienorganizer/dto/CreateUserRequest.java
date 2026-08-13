package com.familienorganizer.dto;

import com.familienorganizer.entity.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank(message = "Name darf nicht leer sein")
        @Size(max = 50, message = "Name darf maximal 50 Zeichen haben")
        String displayName,

        @NotBlank(message = "Farbe darf nicht leer sein")
        @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Farbe muss ein gültiger Hex-Farbwert sein (z.B. #FF5733)")
        String avatarColor,

        @NotNull(message = "Rolle muss angegeben werden")
        Role role,

        @Pattern(regexp = "^[0-9]{4}$", message = "PIN muss aus genau 4 Ziffern bestehen")
        String pinCode
) {}
