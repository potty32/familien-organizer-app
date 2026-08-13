import { Component, inject, OnInit, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { TaskService } from '../../../core/services/task.service';
import { UserService } from '../../../core/services/user.service';
import { SessionService } from '../../../core/services/session.service';
import { User } from '../../../core/models/user.model';

@Component({
  selector: 'app-create-task',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './create-task.component.html',
  styleUrl: './create-task.component.scss'
})
export class CreateTaskComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly taskService = inject(TaskService);
  private readonly userService = inject(UserService);
  private readonly session = inject(SessionService);
  private readonly router = inject(Router);

  readonly users = signal<User[]>([]);
  readonly submitting = signal(false);
  readonly error = signal<string | null>(null);

  readonly isChild = this.session.activeUser()?.role === 'CHILD';

  readonly form = this.fb.group({
    title: ['', [Validators.required, Validators.maxLength(100)]],
    description: [''],
    points: [{ value: 10, disabled: this.isChild }, [Validators.min(0), Validators.max(1000)]],
    assignedToId: ['', Validators.required],
    dueDate: [new Date().toISOString().split('T')[0]],
    recurring: [false],
    recurrencePattern: [null as string | null]
  });

  ngOnInit(): void {
    this.userService.getAll().subscribe({ next: users => this.users.set(users) });

    const activeId = this.session.activeUser()?.id;
    if (activeId) {
      this.form.patchValue({ assignedToId: activeId });
    }

    this.form.get('recurring')!.valueChanges.subscribe(recurring => {
      if (!recurring) this.form.patchValue({ recurrencePattern: null });
    });
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.submitting.set(true);
    this.error.set(null);

    const val = this.form.getRawValue();
    this.taskService.create({
      title: val.title!,
      description: val.description || undefined,
      points: val.points ?? undefined,
      assignedToId: val.assignedToId!,
      dueDate: val.dueDate || undefined,
      recurring: val.recurring ?? false,
      recurrencePattern: (val.recurrencePattern as any) ?? undefined
    }).subscribe({
      next: () => this.router.navigate(['/app/tasks']),
      error: () => {
        this.error.set('Aufgabe konnte nicht gespeichert werden.');
        this.submitting.set(false);
      }
    });
  }

  get recurringEnabled(): boolean {
    return !!this.form.get('recurring')?.value;
  }
}
