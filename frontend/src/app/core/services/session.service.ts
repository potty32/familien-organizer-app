import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { User, SessionRequest } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class SessionService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiBaseUrl}/session`;
  private readonly COOKIE_KEY = 'fo_active_user';

  readonly activeUser = signal<User | null>(null);

  selectProfile(request: SessionRequest): Observable<User> {
    return this.http.post<User>(this.baseUrl, request).pipe(
      tap(user => {
        this.activeUser.set(user);
        this.setCookie(user.id);
      })
    );
  }

  clearSession(): Observable<void> {
    return this.http.delete<void>(this.baseUrl).pipe(
      tap(() => {
        this.activeUser.set(null);
        this.deleteCookie();
      })
    );
  }

  restoreFromCookie(): Observable<User | null> {
    const userId = this.getCookieUserId();
    if (!userId) return of(null);

    return this.http.get<User>(`${environment.apiBaseUrl}/users/${userId}`).pipe(
      tap(user => this.activeUser.set(user)),
      catchError(() => {
        this.deleteCookie();
        return of(null);
      })
    );
  }

  getCookieUserId(): string | null {
    const match = document.cookie.match(
      new RegExp(`(?:^|; )${this.COOKIE_KEY}=([^;]*)`)
    );
    return match ? decodeURIComponent(match[1]) : null;
  }

  isParent(): boolean {
    return this.activeUser()?.role === 'PARENT';
  }

  private setCookie(userId: string): void {
    const expires = new Date();
    expires.setDate(expires.getDate() + 30);
    document.cookie = `${this.COOKIE_KEY}=${encodeURIComponent(userId)}; expires=${expires.toUTCString()}; path=/; SameSite=Strict`;
  }

  private deleteCookie(): void {
    document.cookie = `${this.COOKIE_KEY}=; expires=Thu, 01 Jan 1970 00:00:00 GMT; path=/`;
  }
}
