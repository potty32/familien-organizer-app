import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { map, catchError } from 'rxjs/operators';
import { of } from 'rxjs';
import { SessionService } from '../services/session.service';

export const sessionGuard: CanActivateFn = () => {
  const session = inject(SessionService);
  const router = inject(Router);

  // Session bereits aktiv → direkt durchlassen
  if (session.activeUser()) return true;

  // Kein Cookie → Profilauswahl
  if (!session.getCookieUserId()) return router.createUrlTree(['/']);

  // Cookie vorhanden → Session aus Backend wiederherstellen
  return session.restoreFromCookie().pipe(
    map(user => user ? true : router.createUrlTree(['/'])),
    catchError(() => of(router.createUrlTree(['/'
    ])))
  );
};
