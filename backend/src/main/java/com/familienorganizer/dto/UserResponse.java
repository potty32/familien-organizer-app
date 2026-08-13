package com.familienorganizer.dto;

import com.familienorganizer.entity.Role;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String displayName,
        String avatarColor,
        Role role,
        int totalPoints
) {}
