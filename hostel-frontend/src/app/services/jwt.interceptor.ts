import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from './auth.service';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {

  const authService = inject(AuthService);
  const router = inject(Router);
  const token = authService.getToken();

  // 🚫 Do NOT attach token to auth APIs
  if (req.url.includes('/api/auth')) {
    return next(req);
  }

  // 🔐 Attach JWT if available
  if (token) {
    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  return next(req).pipe(
    catchError(err => {

      // 🔴 Token expired / invalid
      if (err.status === 401 || err.status === 403) {
        authService.logout();

        // ✅ Angular navigation (NO reload, NO loop)
        router.navigate(['/login']);
      }

      return throwError(() => err);
    })
  );
};
