package com.familienorganizer.repository;

import com.familienorganizer.entity.Task;
import com.familienorganizer.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {

    List<Task> findAllByOrderByCreatedAtDesc();

    List<Task> findByAssignedToIdOrderByCreatedAtDesc(UUID assignedToId);

    List<Task> findByStatusOrderByCreatedAtDesc(TaskStatus status);

    @Query("SELECT t FROM Task t WHERE (:status IS NULL OR t.status = :status) " +
           "AND (:assignedToId IS NULL OR t.assignedTo.id = :assignedToId) " +
           "ORDER BY t.createdAt DESC")
    List<Task> findByFilters(@Param("status") TaskStatus status,
                             @Param("assignedToId") UUID assignedToId);
}
