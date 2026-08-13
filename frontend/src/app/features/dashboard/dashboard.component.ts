import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { DashboardService } from '../../core/services/dashboard.service';
import { SessionService } from '../../core/services/session.service';
import { Dashboard } from '../../core/models/dashboard.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
  private readonly dashboardService = inject(DashboardService);
  readonly session = inject(SessionService);

  readonly dashboard = signal<Dashboard | null>(null);
  readonly loading = signal(true);
  readonly error = signal<string | null>(null);

  ngOnInit(): void {
    this.dashboardService.get().subscribe({
      next: data => {
        this.dashboard.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Dashboard konnte nicht geladen werden.');
        this.loading.set(false);
      }
    });
  }

  getMedal(rank: number): string {
    return ['🥇', '🥈', '🥉'][rank - 1] ?? '';
  }

  getInitials(name: string): string {
    return name.split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2);
  }

  isActiveUser(userId: string): boolean {
    return this.session.activeUser()?.id === userId;
  }

  getBarWidth(points: number, max: number): string {
    if (max === 0) return '4px';
    return Math.max(4, Math.round((points / max) * 100)) + '%';
  }
}
