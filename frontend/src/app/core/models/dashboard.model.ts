export interface LeaderboardEntry {
  rank: number;
  userId: string;
  displayName: string;
  avatarColor: string;
  totalPoints: number;
}

export interface Dashboard {
  leaderboard: LeaderboardEntry[];
  statistics: {
    openTasksTotal: number;
    completedToday: number;
    completedThisWeek: number;
  };
}
