package com.familienorganizer.service;

import com.familienorganizer.dto.DashboardResponse;
import com.familienorganizer.dto.DashboardResponse.Statistics;
import com.familienorganizer.dto.LeaderboardEntry;
import com.familienorganizer.dto.PointTransactionResponse;
import com.familienorganizer.entity.TaskStatus;
import com.familienorganizer.repository.FamilyUserRepository;
import com.familienorganizer.repository.PointTransactionRepository;
import com.familienorganizer.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final FamilyUserRepository userRepository;
    private final TaskRepository taskRepository;
    private final PointTransactionRepository pointTransactionRepository;

    public DashboardResponse getDashboard() {
        AtomicInteger rank = new AtomicInteger(1);
        List<LeaderboardEntry> leaderboard = userRepository
                .findByActiveTrueOrderByTotalPointsDesc()
                .stream()
                .map(u -> new LeaderboardEntry(
                        rank.getAndIncrement(),
                        u.getId(),
                        u.getDisplayName(),
                        u.getAvatarColor(),
                        u.getTotalPoints()
                ))
                .toList();

        LocalDateTime startOfDay  = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay    = LocalDate.now().atTime(LocalTime.MAX);
        LocalDateTime startOfWeek = LocalDate.now().with(java.time.DayOfWeek.MONDAY).atStartOfDay();

        Statistics stats = new Statistics(
                taskRepository.countByStatus(TaskStatus.OPEN),
                taskRepository.countByStatusAndUpdatedAtBetween(TaskStatus.DONE, startOfDay, endOfDay),
                taskRepository.countByStatusAndUpdatedAtBetween(TaskStatus.DONE, startOfWeek, endOfDay)
        );

        return new DashboardResponse(leaderboard, stats);
    }

    public List<PointTransactionResponse> getPointHistory(UUID userId) {
        return pointTransactionRepository
                .findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(pt -> new PointTransactionResponse(
                        pt.getId(),
                        pt.getPoints(),
                        pt.getReason(),
                        pt.getTask() != null ? pt.getTask().getId() : null,
                        pt.getCreatedAt()
                ))
                .toList();
    }
}
