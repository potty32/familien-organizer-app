package com.familienorganizer.controller;

import com.familienorganizer.dto.CreateTaskRequest;
import com.familienorganizer.dto.TaskResponse;
import com.familienorganizer.dto.UpdateTaskStatusRequest;
import com.familienorganizer.entity.TaskStatus;
import com.familienorganizer.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public List<TaskResponse> getAll(
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) UUID assignedTo) {
        return taskService.getAll(status, assignedTo);
    }

    @GetMapping("/mine")
    public List<TaskResponse> getMyTasks(
            @RequestHeader("X-Active-User-Id") UUID activeUserId) {
        return taskService.getMyTasks(activeUserId);
    }

    @GetMapping("/{id}")
    public TaskResponse getById(@PathVariable UUID id) {
        return taskService.getById(id);
    }

    @PostMapping
    public ResponseEntity<TaskResponse> create(
            @Valid @RequestBody CreateTaskRequest request,
            @RequestHeader("X-Active-User-Id") UUID activeUserId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.create(request, activeUserId));
    }

    @PutMapping("/{id}")
    public TaskResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody CreateTaskRequest request) {
        return taskService.update(id, request);
    }

    @PatchMapping("/{id}/status")
    public TaskResponse updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTaskStatusRequest request) {
        return taskService.updateStatus(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
