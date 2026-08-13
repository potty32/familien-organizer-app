import { Component, inject, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { UserService } from '../../core/services/user.service';
import { SessionService } from '../../core/services/session.service';
import { User } from '../../core/models/user.model';

@Component({
  selector: 'app-profile-select',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './profile-select.component.html',
  styleUrl: './profile-select.component.scss'
})
export class ProfileSelectComponent implements OnInit {
  private readonly userService = inject(UserService);
  private readonly sessionService = inject(SessionService);
  private readonly router = inject(Router);

  readonly users = signal<User[]>([]);
  readonly loading = signal(true);
  readonly error = signal<string | null>(null);

  ngOnInit(): void {
    this.userService.getAll().subscribe({
      next: users => {
        this.users.set(users);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Familienmitglieder konnten nicht geladen werden.');
        this.loading.set(false);
      }
    });
  }

  selectProfile(user: User): void {
    this.sessionService.selectProfile({ userId: user.id }).subscribe({
      next: () => this.router.navigate(['/app/dashboard']),
      error: () => this.error.set('Profil konnte nicht ausgewählt werden.')
    });
  }

  getInitials(name: string): string {
    return name
      .split(' ')
      .map(n => n[0])
      .join('')
      .toUpperCase()
      .slice(0, 2);
  }
}
