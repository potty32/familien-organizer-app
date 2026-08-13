import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { MealWishService } from '../../core/services/meal-wish.service';
import { SessionService } from '../../core/services/session.service';
import { MealWish, WeekDay } from '../../core/models/meal-wish.model';

type ViewMode = 'wishes' | 'plan';

@Component({
  selector: 'app-meal-wishes',
  standalone: true,
  imports: [CommonModule, DatePipe, FormsModule, ReactiveFormsModule],
  templateUrl: './meal-wishes.component.html',
  styleUrl: './meal-wishes.component.scss'
})
export class MealWishesComponent implements OnInit {
  private readonly service = inject(MealWishService);
  readonly session = inject(SessionService);
  private readonly fb = inject(FormBuilder);

  readonly view = signal<ViewMode>('wishes');
  readonly wishes = signal<MealWish[]>([]);
  readonly weeklyMeals = signal<MealWish[]>([]);
  readonly loading = signal(true);
  readonly error = signal<string | null>(null);
  readonly showNewForm = signal(false);
  readonly acceptingId = signal<string | null>(null);
  readonly acceptDate = signal('');

  readonly newForm = this.fb.group({
    name: ['', [Validators.required, Validators.maxLength(100)]],
    description: ['']
  });

  readonly pendingWishes = computed(() => this.wishes().filter(w => w.status === 'PENDING'));
  readonly acceptedWishes = computed(() => this.wishes().filter(w => w.status === 'ACCEPTED'));
  readonly rejectedWishes = computed(() => this.wishes().filter(w => w.status === 'REJECTED'));

  readonly weekDays = computed((): WeekDay[] => {
    const today = new Date(); today.setHours(0, 0, 0, 0);
    const meals = this.weeklyMeals();
    const days: WeekDay[] = [];
    const DAY_NAMES = ['So', 'Mo', 'Di', 'Mi', 'Do', 'Fr', 'Sa'];
    const DAY_FULL  = ['Sonntag','Montag','Dienstag','Mittwoch','Donnerstag','Freitag','Samstag'];

    // Build 2 weeks starting from this Monday
    const monday = new Date(today);
    monday.setDate(today.getDate() - ((today.getDay() + 6) % 7));

    for (let i = 0; i < 14; i++) {
      const d = new Date(monday); d.setDate(monday.getDate() + i);
      const iso = d.toISOString().split('T')[0];
      const dayMeals = meals.filter(m => m.weeklyPlanDate === iso);
      if (dayMeals.length > 0 || d >= today) {
        days.push({
          date: iso,
          label: DAY_FULL[d.getDay()],
          shortLabel: DAY_NAMES[d.getDay()],
          isToday: d.getTime() === today.getTime(),
          meals: dayMeals
        });
      }
    }
    return days;
  });

  readonly thisWeekDays = computed(() => this.weekDays().slice(0, 7));
  readonly nextWeekDays = computed(() => this.weekDays().slice(7).filter(d => d.meals.length > 0));

  ngOnInit(): void { this.loadAll(); }

  setView(v: ViewMode): void { this.view.set(v); }

  toggleNewForm(): void {
    this.showNewForm.update(v => !v);
    if (!this.showNewForm()) this.newForm.reset();
  }

  submitWish(): void {
    if (this.newForm.invalid) { this.newForm.markAllAsTouched(); return; }
    const val = this.newForm.value;
    this.service.create({ name: val.name!, description: val.description || undefined }).subscribe({
      next: w => {
        this.wishes.update(ws => [w, ...ws]);
        this.newForm.reset();
        this.showNewForm.set(false);
      },
      error: () => this.error.set('Wunsch konnte nicht gespeichert werden.')
    });
  }

  startAccept(id: string): void {
    this.acceptingId.set(id);
    // Default: nächster Montag oder heute
    const today = new Date();
    const monday = new Date(today);
    monday.setDate(today.getDate() - ((today.getDay() + 6) % 7));
    this.acceptDate.set(monday.toISOString().split('T')[0]);
  }

  cancelAccept(): void { this.acceptingId.set(null); }

  confirmAccept(id: string): void {
    if (!this.acceptDate()) return;
    this.service.accept(id, { weeklyPlanDate: this.acceptDate() }).subscribe({
      next: updated => {
        this.wishes.update(ws => ws.map(w => w.id === id ? updated : w));
        this.acceptingId.set(null);
        this.loadWeeklyPlan();
      },
      error: () => this.error.set('Fehler beim Akzeptieren.')
    });
  }

  reject(id: string): void {
    this.service.reject(id).subscribe({
      next: updated => this.wishes.update(ws => ws.map(w => w.id === id ? updated : w)),
      error: () => this.error.set('Fehler beim Ablehnen.')
    });
  }

  getInitials(name: string): string {
    return name.split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2);
  }

  get isParent(): boolean { return this.session.activeUser()?.role === 'PARENT'; }

  private loadAll(): void {
    this.loading.set(true);
    this.service.getAll().subscribe({
      next: ws => { this.wishes.set(ws); this.loading.set(false); },
      error: () => { this.error.set('Wünsche konnten nicht geladen werden.'); this.loading.set(false); }
    });
    this.loadWeeklyPlan();
  }

  private loadWeeklyPlan(): void {
    this.service.getWeeklyPlan().subscribe({ next: ms => this.weeklyMeals.set(ms) });
  }
}
