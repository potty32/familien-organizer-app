package com.familienorganizer.dto;

import java.util.UUID;

public record TaskUserRef(
        UUID id,
        String displayName,
        String avatarColor
) {}
