import { inject } from '@angular/core';
import { Router, UrlTree } from '@angular/router';
import { AuthService } from './services/auth.service';

// ===============================
// 🔐 AUTH GUARD
// ===============================
export const authGuard = (): boolean | UrlTree => {
  const auth = inject(AuthService);
  const router = inject(Router);

  return auth.isLoggedIn()
    ? true
    : router.createUrlTree(['/login']); // ✅ FIXED
};

// ===============================
// 🧑‍🎓 STUDENT GUARD
// ===============================
export const studentGuard = (): boolean | UrlTree => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (!auth.isLoggedIn()) {
    return router.createUrlTree(['/login']); // ✅ FIXED
  }

  return auth.getRole() === 'STUDENT'
    ? true
    : router.createUrlTree(['/login']);
};

// ===============================
// 🧑‍🏫 WARDEN GUARD
// ===============================
export const wardenGuard = (): boolean | UrlTree => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (!auth.isLoggedIn()) {
    return router.createUrlTree(['/login']); // ✅ FIXED
  }

  return auth.getRole() === 'WARDEN'
    ? true
    : router.createUrlTree(['/login']);
};

// ===============================
// 🧑‍💼 ADMIN GUARD
// ===============================
export const adminGuard = (): boolean | UrlTree => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (!auth.isLoggedIn()) {
    return router.createUrlTree(['/login']); // ✅ FIXED
  }

  return auth.getRole() === 'ADMIN'
    ? true
    : router.createUrlTree(['/login']);
};
