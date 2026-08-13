import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { User, SessionRequest } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class SessionService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiBaseUrl}/session`;

  readonly activeUser = signal<User | null>(null);

  selectProfile(request: SessionRequest): Observable<User> {
    return this.http.post<User>(this.baseUrl, request).pipe(
      tap(user => this.activeUser.set(user))
    );
  }

  clearSession(): Observable<void> {
    return this.http.delete<void>(this.baseUrl).pipe(
      tap(() => this.activeUser.set(null))
    );
  }

  isParent(): boolean {
    return this.activeUser()?.role === 'PARENT';
  }
}
