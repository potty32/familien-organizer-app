import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { SessionService } from '../services/session.service';

export const sessionInterceptor: HttpInterceptorFn = (req, next) => {
  const sessionService = inject(SessionService);
  const activeUser = sessionService.activeUser();

  if (activeUser) {
    const cloned = req.clone({
      setHeaders: { 'X-Active-User-Id': activeUser.id }
    });
    return next(cloned);
  }

  return next(req);
};
