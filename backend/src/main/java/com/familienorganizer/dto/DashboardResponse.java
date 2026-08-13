package com.familienorganizer.dto;

import java.util.List;

public record DashboardResponse(
        List<LeaderboardEntry> leaderboard,
        Statistics statistics
) {
    public record Statistics(
            long openTasksTotal,
            long completedToday,
            long completedThisWeek
    ) {}
}
