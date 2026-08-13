import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { TaskService } from '../../../core/services/task.service';
import { SessionService } from '../../../core/services/session.service';
import { Task, TaskStatus } from '../../../core/models/task.model';
import { TaskCardComponent } from '../task-card/task-card.component';

type FilterMode = 'all' | 'mine';

@Component({
  selector: 'app-task-list',
  standalone: true,
  imports: [CommonModule, RouterLink, TaskCardComponent],
  templateUrl: './task-list.component.html',
  styleUrl: './task-list.component.scss'
})
export class TaskListComponent implements OnInit {
  private readonly taskService = inject(TaskService);
  private readonly session = inject(SessionService);
  private readonly router = inject(Router);

  readonly tasks = signal<Task[]>([]);
  readonly loading = signal(true);
  readonly error = signal<string | null>(null);
  readonly filterMode = signal<FilterMode>('all');

  readonly filteredTasks = computed(() => {
    const all = this.tasks();
    const mode = this.filterMode();
    const activeId = this.session.activeUser()?.id;
    if (mode === 'mine' && activeId) {
      return all.filter(t => t.assignedTo.id === activeId);
    }
    return all;
  });

  readonly openTasks = computed(() => this.filteredTasks().filter(t => t.status === 'OPEN'));
  readonly inProgressTasks = computed(() => this.filteredTasks().filter(t => t.status === 'IN_PROGRESS'));
  readonly doneTasks = computed(() => this.filteredTasks().filter(t => t.status === 'DONE'));

  ngOnInit(): void {
    this.loadTasks();
  }

  setFilter(mode: FilterMode): void {
    this.filterMode.set(mode);
  }

  onStatusChange(event: { taskId: string; status: TaskStatus }): void {
    this.taskService.updateStatus(event.taskId, event.status).subscribe({
      next: updated => {
        this.tasks.update(tasks =>
          tasks.map(t => t.id === updated.id ? updated : t)
        );
      },
      error: () => this.error.set('Status konnte nicht geändert werden.')
    });
  }

  onDelete(taskId: string): void {
    this.taskService.delete(taskId).subscribe({
      next: () => this.tasks.update(tasks => tasks.filter(t => t.id !== taskId)),
      error: () => this.error.set('Aufgabe konnte nicht gelöscht werden.')
    });
  }

  private loadTasks(): void {
    this.loading.set(true);
    this.taskService.getAll().subscribe({
      next: tasks => {
        this.tasks.set(tasks);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Aufgaben konnten nicht geladen werden.');
        this.loading.set(false);
      }
    });
  }
}
