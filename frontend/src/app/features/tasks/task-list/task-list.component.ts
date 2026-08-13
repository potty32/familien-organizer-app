import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { TaskService } from '../../../core/services/task.service';
import { SessionService } from '../../../core/services/session.service';
import { Task, TaskStatus } from '../../../core/models/task.model';
import { TaskCardComponent } from '../task-card/task-card.component';

type FilterMode = 'all' | 'mine';

export interface TaskGroup {
  label: string;
  icon: string;
  colorClass: string;
  tasks: Task[];
}

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

  readonly tasks = signal<Task[]>([]);
  readonly loading = signal(true);
  readonly error = signal<string | null>(null);
  readonly filterMode = signal<FilterMode>('all');
  readonly showDone = signal(false);

  readonly filteredTasks = computed(() => {
    const all = this.tasks();
    const activeId = this.session.activeUser()?.id;
    if (this.filterMode() === 'mine' && activeId) {
      return all.filter(t => t.assignedTo.id === activeId);
    }
    return all;
  });

  readonly taskGroups = computed((): TaskGroup[] => {
    const active = this.filteredTasks().filter(t => t.status !== 'DONE');

    const today = new Date(); today.setHours(0, 0, 0, 0);
    const tomorrow = new Date(today); tomorrow.setDate(today.getDate() + 1);
    const in7days = new Date(today); in7days.setDate(today.getDate() + 7);

    const buckets: Record<string, Task[]> = {
      overdue: [], today: [], tomorrow: [], week: [], later: [], noDate: []
    };

    for (const task of active) {
      if (!task.dueDate) { buckets['noDate'].push(task); continue; }
      const due = new Date(task.dueDate); due.setHours(0, 0, 0, 0);
      if (due < today)                              buckets['overdue'].push(task);
      else if (due.getTime() === today.getTime())   buckets['today'].push(task);
      else if (due.getTime() === tomorrow.getTime()) buckets['tomorrow'].push(task);
      else if (due <= in7days)                      buckets['week'].push(task);
      else                                          buckets['later'].push(task);
    }

    const groups: TaskGroup[] = [
      { label: 'Überfällig',    icon: '🔴', colorClass: 'group--overdue',   tasks: buckets['overdue'] },
      { label: 'Heute',         icon: '📌', colorClass: 'group--today',     tasks: buckets['today'] },
      { label: 'Morgen',        icon: '📅', colorClass: 'group--tomorrow',  tasks: buckets['tomorrow'] },
      { label: 'Diese Woche',   icon: '📆', colorClass: 'group--week',      tasks: buckets['week'] },
      { label: 'Später',        icon: '🗓️', colorClass: 'group--later',     tasks: buckets['later'] },
      { label: 'Ohne Datum',    icon: '📋', colorClass: 'group--nodate',    tasks: buckets['noDate'] },
    ];

    return groups.filter(g => g.tasks.length > 0);
  });

  readonly doneTasks = computed(() => this.filteredTasks().filter(t => t.status === 'DONE'));

  readonly activeTaskCount = computed(() =>
    this.filteredTasks().filter(t => t.status !== 'DONE').length
  );

  ngOnInit(): void { this.loadTasks(); }

  setFilter(mode: FilterMode): void { this.filterMode.set(mode); }

  toggleDone(): void { this.showDone.update(v => !v); }

  onStatusChange(event: { taskId: string; status: TaskStatus }): void {
    this.taskService.updateStatus(event.taskId, event.status).subscribe({
      next: () => this.loadTasks(),
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
      next: tasks => { this.tasks.set(tasks); this.loading.set(false); },
      error: () => { this.error.set('Aufgaben konnten nicht geladen werden.'); this.loading.set(false); }
    });
  }
}
