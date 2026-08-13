import { Component, inject, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../core/services/user.service';
import { SessionService } from '../../core/services/session.service';
import { User } from '../../core/models/user.model';

@Component({
  selector: 'app-profile-select',
  standalone: true,
  imports: [CommonModule, FormsModule],
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

  // PIN-Dialog
  readonly pendingUser = signal<User | null>(null);
  pin = '';
  pinError = '';

  ngOnInit(): void {
    this.userService.getAll().subscribe({
      next: users => { this.users.set(users); this.loading.set(false); },
      error: () => { this.error.set('Familienmitglieder konnten nicht geladen werden.'); this.loading.set(false); }
    });
  }

  onCardClick(user: User): void {
    if (user.role === 'PARENT') {
      this.pendingUser.set(user);
      this.pin = '';
      this.pinError = '';
    } else {
      this.confirm(user);
    }
  }

  submitPin(): void {
    const user = this.pendingUser();
    if (!user) return;
    this.confirm(user, this.pin);
  }

  cancelPin(): void {
    this.pendingUser.set(null);
    this.pin = '';
    this.pinError = '';
  }

  private confirm(user: User, pinCode?: string): void {
    this.sessionService.selectProfile({ userId: user.id, pinCode }).subscribe({
      next: (activeUser) => this.router.navigate([
        activeUser.role === 'CHILD' ? '/app/dashboard' : '/app/tasks'
      ]),
      error: () => {
        if (pinCode !== undefined) {
          this.pinError = 'PIN ist nicht korrekt.';
        } else {
          this.error.set('Profil konnte nicht ausgewählt werden.');
        }
      }
    });
  }

  getInitials(name: string): string {
    return name.split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2);
  }
}
