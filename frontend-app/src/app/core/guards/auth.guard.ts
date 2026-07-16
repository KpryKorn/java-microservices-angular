import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { map } from 'rxjs';
import { AuthService } from '../services/auth.service';

/** Protège les routes nécessitant une session active ; sinon redirige vers /login. */
export const authGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return authService
    .checkSession()
    .pipe(map((user) => (user ? true : router.createUrlTree(['/login']))));
};
