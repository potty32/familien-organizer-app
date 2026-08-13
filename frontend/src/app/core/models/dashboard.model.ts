export interface LeaderboardEntry {
  rank: number;
  userId: string;
  displayName: string;
  avatarColor: string;
  totalPoints: number;
}

export interface DashboardStatistics {
  openTasksTotal: number;
  completedToday: number;
  completedThisWeek: number;
}

export interface Dashboard {
  leaderboard: LeaderboardEntry[];
  statistics: DashboardStatistics;
}

export interface PointTransactionResponse {
  id: string;
  points: number;
  reason: string;
  taskId: string | null;
  createdAt: string;
}
