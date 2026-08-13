import { Component, input, output } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { Task, TaskStatus } from '../../../core/models/task.model';

@Component({
  selector: 'app-task-card',
  standalone: true,
  imports: [CommonModule, DatePipe],
  templateUrl: './task-card.component.html',
  styleUrl: './task-card.component.scss'
})
export class TaskCardComponent {
  readonly task = input.required<Task>();
  readonly statusChange = output<{ taskId: string; status: TaskStatus }>();
  readonly deleteTask = output<string>();

  changeStatus(status: TaskStatus): void {
    this.statusChange.emit({ taskId: this.task().id, status });
  }

  onDelete(): void {
    this.deleteTask.emit(this.task().id);
  }

  getInitials(name: string): string {
    return name.split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2);
  }

  get statusLabel(): string {
    const labels: Record<TaskStatus, string> = {
      OPEN: 'Offen',
      IN_PROGRESS: 'In Arbeit',
      DONE: 'Erledigt'
    };
    return labels[this.task().status];
  }
}
