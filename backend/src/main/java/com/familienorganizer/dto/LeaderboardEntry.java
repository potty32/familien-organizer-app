package com.familienorganizer.dto;

import java.util.UUID;

public record LeaderboardEntry(
        int rank,
        UUID userId,
        String displayName,
        String avatarColor,
        int totalPoints
) {}
