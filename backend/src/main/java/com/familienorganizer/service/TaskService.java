package com.familienorganizer.service;

import com.familienorganizer.dto.CreateTaskRequest;
import com.familienorganizer.dto.TaskResponse;
import com.familienorganizer.dto.TaskUserRef;
import com.familienorganizer.dto.UpdateTaskStatusRequest;
import com.familienorganizer.entity.FamilyUser;
import com.familienorganizer.entity.PointTransaction;
import com.familienorganizer.entity.Task;
import com.familienorganizer.entity.TaskStatus;
import com.familienorganizer.repository.FamilyUserRepository;
import com.familienorganizer.repository.PointTransactionRepository;
import com.familienorganizer.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskService {

    private final TaskRepository taskRepository;
    private final FamilyUserRepository userRepository;
    private final PointTransactionRepository pointTransactionRepository;

    public List<TaskResponse> getAll(TaskStatus status, UUID assignedToId) {
        return taskRepository.findByFilters(status, assignedToId)
                .stream().map(this::toResponse).toList();
    }

    public List<TaskResponse> getMyTasks(UUID activeUserId) {
        return taskRepository.findByAssignedToIdOrderByCreatedAtDesc(activeUserId)
                .stream().map(this::toResponse).toList();
    }

    public TaskResponse getById(UUID id) {
        return toResponse(findOrThrow(id));
    }

    @Transactional
    public TaskResponse create(CreateTaskRequest request, UUID createdById) {
        FamilyUser assignedTo = findUserOrThrow(request.assignedToId());
        FamilyUser createdBy  = findUserOrThrow(createdById);

        Task task = Task.builder()
                .title(request.title())
                .description(request.description())
                .points(request.points())
                .assignedTo(assignedTo)
                .createdBy(createdBy)
                .dueDate(request.dueDate())
                .recurring(request.recurring())
                .recurrencePattern(request.recurrencePattern())
                .build();

        return toResponse(taskRepository.save(task));
    }

    @Transactional
    public TaskResponse update(UUID id, CreateTaskRequest request) {
        Task task = findOrThrow(id);
        FamilyUser assignedTo = findUserOrThrow(request.assignedToId());

        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setPoints(request.points());
        task.setAssignedTo(assignedTo);
        task.setDueDate(request.dueDate());
        task.setRecurring(request.recurring());
        task.setRecurrencePattern(request.recurrencePattern());

        return toResponse(taskRepository.save(task));
    }

    @Transactional
    public TaskResponse updateStatus(UUID id, UpdateTaskStatusRequest request) {
        Task task = findOrThrow(id);
        TaskStatus oldStatus = task.getStatus();
        TaskStatus newStatus = request.status();

        if (oldStatus == newStatus) {
            return toResponse(task);
        }

        // Punkte-Gutschrift beim Wechsel ZU DONE
        if (newStatus == TaskStatus.DONE && task.getPoints() != null) {
            FamilyUser user = task.getAssignedTo();
            user.setTotalPoints(user.getTotalPoints() + task.getPoints());
            userRepository.save(user);
            pointTransactionRepository.save(PointTransaction.builder()
                    .user(user).task(task).points(task.getPoints())
                    .reason("Aufgabe '" + task.getTitle() + "' abgeschlossen")
                    .build());
        }

        // Punkte-Abzug beim Wechsel VON DONE weg
        if (oldStatus == TaskStatus.DONE && task.getPoints() != null) {
            FamilyUser user = task.getAssignedTo();
            int deducted = Math.min(task.getPoints(), user.getTotalPoints());
            user.setTotalPoints(user.getTotalPoints() - deducted);
            userRepository.save(user);
            pointTransactionRepository.save(PointTransaction.builder()
                    .user(user).task(task).points(-deducted)
                    .reason("Aufgabe '" + task.getTitle() + "' wieder geöffnet")
                    .build());
        }

        task.setStatus(newStatus);
        TaskResponse result = toResponse(taskRepository.save(task));

        // Neue Instanz sofort erstellen, wenn wiederkehrende Aufgabe erledigt wird
        if (newStatus == TaskStatus.DONE && task.isRecurring() && task.getRecurrencePattern() != null) {
            createNextRecurringTask(task);
        }

        return result;
    }

    private void createNextRecurringTask(Task completed) {
        LocalDate base = completed.getDueDate() != null ? completed.getDueDate() : LocalDate.now();
        LocalDate nextDue = switch (completed.getRecurrencePattern()) {
            case DAILY   -> base.plusDays(1);
            case WEEKLY  -> base.plusWeeks(1);
            case MONTHLY -> base.plusMonths(1);
        };

        taskRepository.save(Task.builder()
                .title(completed.getTitle())
                .description(completed.getDescription())
                .points(completed.getPoints())
                .assignedTo(completed.getAssignedTo())
                .createdBy(completed.getCreatedBy())
                .dueDate(nextDue)
                .recurring(true)
                .recurrencePattern(completed.getRecurrencePattern())
                .build());
    }

    @Transactional
    public void delete(UUID id) {
        Task task = findOrThrow(id);
        // Punkte zurückbuchen falls Aufgabe bereits erledigt war
        if (task.getStatus() == TaskStatus.DONE && task.getPoints() != null) {
            FamilyUser user = task.getAssignedTo();
            int newTotal = Math.max(0, user.getTotalPoints() - task.getPoints());
            user.setTotalPoints(newTotal);
            userRepository.save(user);
        }
        taskRepository.delete(task);
    }

    private Task findOrThrow(UUID id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Aufgabe nicht gefunden: " + id));
    }

    private FamilyUser findUserOrThrow(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Benutzer nicht gefunden: " + id));
    }

    private TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPoints(),
                toUserRef(task.getAssignedTo()),
                toUserRef(task.getCreatedBy()),
                task.getDueDate(),
                task.isRecurring(),
                task.getRecurrencePattern(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }

    private TaskUserRef toUserRef(FamilyUser user) {
        return new TaskUserRef(user.getId(), user.getDisplayName(), user.getAvatarColor());
    }
}
